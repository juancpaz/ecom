package com.juancpaz.ecom.tools.command.exceptions;

import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper=false)
public class HealthCheckException extends CommandException {
	private HealthCheck healthCheck = null;
	
	public HealthCheckException() {
		super();
	}
	public HealthCheckException(String message) {
		super(message);
	}

	public HealthCheckException(HealthCheck healthCheck, Exception e) {
		super(e);
		this.healthCheck = healthCheck;
	}

	public HealthCheckException(Exception e) {
		super(e);
	}
}
