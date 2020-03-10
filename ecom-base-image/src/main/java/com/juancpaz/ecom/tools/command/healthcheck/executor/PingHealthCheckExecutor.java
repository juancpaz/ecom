package com.juancpaz.ecom.tools.command.healthcheck.executor;

import java.net.Socket;

import org.springframework.stereotype.Component;

import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;
import com.juancpaz.ecom.tools.command.healthcheck.model.PingHealthCheck;

@Component
public class PingHealthCheckExecutor extends AbstractHealthCheckExecutor {
	@Override
	protected Boolean executeHeathCheck(HealthCheck healthCheck) throws Exception {

		PingHealthCheck pingHealthCheck = (PingHealthCheck) healthCheck;
		boolean result = false;
		try (Socket socket = new Socket(pingHealthCheck.getHostname(),  pingHealthCheck.getPort())) {
			result = true;
		}

		return result;
	}

}
