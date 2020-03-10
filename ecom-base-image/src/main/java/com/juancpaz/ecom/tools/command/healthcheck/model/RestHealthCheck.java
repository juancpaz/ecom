package com.juancpaz.ecom.tools.command.healthcheck.model;

import java.util.List;

import com.juancpaz.ecom.tools.command.healthcheck.HealthCheckType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RestHealthCheck extends HealthCheck {
	@Data
	public static class Field {
		private String name;
		private String value;
	}
	@Data
	public static class Expected {
		private String response;
		private List<Field> fields;
	}
	private String url;
	private Expected expected;
	
	public RestHealthCheck() {
		super(HealthCheckType.REST);
	}
}
