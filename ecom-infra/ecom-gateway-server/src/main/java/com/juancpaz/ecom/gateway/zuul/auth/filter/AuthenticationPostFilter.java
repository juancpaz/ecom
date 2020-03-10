package com.juancpaz.ecom.gateway.zuul.auth.filter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.juancpaz.ecom.common.security.dto.TokenDTO;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class AuthenticationPostFilter extends ZuulFilter {

	  private static Logger log = LoggerFactory.getLogger(AuthenticationPostFilter.class);

	  public static final String MAGIC_KEY = "obfuscate";

	  @Autowired
	  private CacheManager cacheManager;

	  @Override
	  public String filterType() {
	    return "post";
	  }

	  @Override
	  public int filterOrder() {
	    return 1;
	  }

	  @Override
	  public boolean shouldFilter() {
		  RequestContext ctx = RequestContext.getCurrentContext();
			HttpServletRequest request = ctx.getRequest();
	       return request.getParameter("grant_type")!=null &&  (StringUtils.equals(request.getParameter("grant_type"),"password"));
	  }

	  @Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		if (request.getRequestURL().indexOf("/oauth/token") > 0 ) {
			log.info("SessionId of the request in post filter  :::::" + request.getSession().getId());
			HttpServletResponse response = ctx.getResponse();
			if (response.getStatus() == HttpServletResponse.SC_OK) {
				try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
					final String responseData = CharStreams
							.toString(new InputStreamReader(responseDataStream, "UTF-8"));
					TokenDTO tokenDTO = new ObjectMapper().readValue(responseData, TokenDTO.class);
					log.info("User ="+request.getParameter("username")+ "  Token  :::::"+tokenDTO.getAccess_token());
					long expiryTime=System.currentTimeMillis()+((Integer.valueOf(tokenDTO.getExpires_in())-1)*1000);
					String refToken=expiryTime+":"+createReferenceToken(request.getParameter("username"),expiryTime );
					log.info("Reference token ="+refToken);
					ctx.addZuulResponseHeader("Access-Control-Expose-Headers", "x-token");
					ctx.addZuulResponseHeader("x-token", refToken);
					cacheManager.getCache("AUTH_CACHE").put(refToken, tokenDTO);
				} catch (Exception e) {
					log.warn("Error reading body", e);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}


			}
		}

		return null;
	}


	  private  String createReferenceToken(String username, long expires){
	        StringBuilder signatureBuilder = new StringBuilder();
	        signatureBuilder.append(username);
	        signatureBuilder.append(":");
	        signatureBuilder.append(expires);
	        signatureBuilder.append(":");
	        signatureBuilder.append(MAGIC_KEY);
	        MessageDigest digest;
	        try {
	            digest = MessageDigest.getInstance("MD5");
	        } catch (NoSuchAlgorithmException e) {
	            throw new IllegalStateException("No MD5 algorithm available!");
	        }
	        log.info(new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes()))));
	        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
	    }


	}
