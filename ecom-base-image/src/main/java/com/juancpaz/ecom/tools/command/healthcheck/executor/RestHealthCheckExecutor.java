package com.juancpaz.ecom.tools.command.healthcheck.executor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;
import com.juancpaz.ecom.tools.command.healthcheck.model.RestHealthCheck;
import com.juancpaz.ecom.tools.command.healthcheck.model.RestHealthCheck.Field;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestHealthCheckExecutor extends AbstractHealthCheckExecutor implements HealthCheckExecutor {
	@Override
	protected Boolean executeHeathCheck(HealthCheck healthCheck) throws Exception {
		try {
			RestHealthCheck restHealthCheck = (RestHealthCheck) healthCheck;
			String url = restHealthCheck.getUrl();
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				return false;
			}
			if (restHealthCheck.getExpected().getFields() != null) {
				return check(response.getBody(), restHealthCheck.getExpected().getFields());
			} else if (restHealthCheck.getExpected().getResponse() != null) {
				return check(response.getBody(), restHealthCheck.getExpected().getResponse());
			}
			return false;
		} catch (Exception e) {
			throw e;
		}
	}

	private Boolean check(String body, List<Field> fields) throws Exception {
		for (Field field: fields) {
			String actualValue = getJsonFieldValue(body, field.getName());
			String expectedValue = field.getValue();
			if (!actualValue.equalsIgnoreCase(expectedValue)) {
				return false;
			}
		}
		return true;
	}

	private String getJsonFieldValue(String body, String name) throws Exception {
		String value = null; 
		JsonParser jsonParser = null;
		try {
			jsonParser = new JsonFactory().createParser(body);
			while(!jsonParser.isClosed()){
			    JsonToken jsonToken = jsonParser.nextToken();
			    if(JsonToken.FIELD_NAME.equals(jsonToken)) {
			    	String fieldName = jsonParser.getCurrentName();
			    	if (fieldName.equalsIgnoreCase(name)) {
			    		jsonParser.nextToken();
		                value = jsonParser.getValueAsString();
			    		break;
			    	}
			    }
			}
			return value;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	private Boolean check(String body, String expected) {
		String actual = body.replaceAll("\n", "").replaceAll("  ", "").replaceAll(",", ", ");
		if (!actual.equals(expected)) {
			return false;
		}
		return true;
	}
}
