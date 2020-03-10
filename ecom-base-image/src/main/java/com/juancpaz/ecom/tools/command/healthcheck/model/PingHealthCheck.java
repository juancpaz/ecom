package com.juancpaz.ecom.tools.command.healthcheck.model;

import com.juancpaz.ecom.tools.command.healthcheck.HealthCheckType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PingHealthCheck extends HealthCheck {
	private String hostname;
	private int port;
	
	public PingHealthCheck() {
		super(HealthCheckType.PING);
	}
}
