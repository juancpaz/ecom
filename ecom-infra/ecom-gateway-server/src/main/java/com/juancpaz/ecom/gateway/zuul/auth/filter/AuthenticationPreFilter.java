package com.juancpaz.ecom.gateway.zuul.auth.filter;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.juancpaz.ecom.common.security.dto.TokenDTO;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class AuthenticationPreFilter extends ZuulFilter {

	  private static Logger log = LoggerFactory.getLogger(AuthenticationPreFilter.class);


	  @Override
	  public String filterType() {
	    return "pre";
	  }

	  @Override
	  public int filterOrder() {
	    return 1;
	  }

	  @Override
	  public boolean shouldFilter() {
	    return true;
	  }


     @Autowired
	 private CacheManager cacheManager;

	  @Override
	  public Object run() {
	    RequestContext ctx = RequestContext.getCurrentContext();
	    HttpServletRequest request = ctx.getRequest();
	    log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
	    if(request.getRequestURL().indexOf("/oauth/token")>0 && request.getParameter("grant_type")!=null &&  (StringUtils.equals(request.getParameter("grant_type"),"password"))){
	          ctx.addZuulRequestHeader("authorization", "Basic " +new String(Base64.getEncoder().encode("ecom_app:ecom".getBytes())));
	          ctx.addZuulRequestHeader("content-type", "application/x-www-form-urlencoded; charset=utf-8");
	    }else{
	    	final String xToken=request.getHeader("x-token");
	    	if(StringUtils.isNotEmpty(xToken)){
	    		ValueWrapper value=cacheManager.getCache("AUTH_CACHE").get(xToken);

	    		if(value!=null){
	    			TokenDTO tokenDTO=(TokenDTO)value.get();
	    			log.info("Access token retrived for ref - token="+xToken);
	    			ctx.addZuulRequestHeader("Authorization", "Bearer  " +tokenDTO.getAccess_token());
	    		}
	    	}
	    }
	    return null;

	  }
	}

