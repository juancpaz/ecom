package com.juancpaz.ecom.tools.command.healthcheck.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.juancpaz.ecom.tools.command.healthcheck.HealthCheckType;

import lombok.Data;

@Data
@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME, 
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "type")
		@JsonSubTypes({ 
		  @Type(value = PingHealthCheck.class, name = "PING"), 
		  @Type(value = RestHealthCheck.class, name = "REST") 
		})
public class HealthCheck {
	private HealthCheckType type;
	private String name;
	private String description;
	private int delay;
	private int timeout;
	
	public HealthCheck(HealthCheckType type) {
		this.type = type;
	}
}
