package com.juancpaz.ecom.tools.command.healthcheck.executor;

import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;

public interface HealthCheckExecutor {

	public Boolean execute(HealthCheck healthCheck) throws HealthCheckException;
}
