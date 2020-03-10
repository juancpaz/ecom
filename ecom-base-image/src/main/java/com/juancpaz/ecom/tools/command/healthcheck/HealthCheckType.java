package com.juancpaz.ecom.tools.command.healthcheck;

public enum HealthCheckType {
	PING("ping"), REST("rest");
	private String type;

	HealthCheckType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
