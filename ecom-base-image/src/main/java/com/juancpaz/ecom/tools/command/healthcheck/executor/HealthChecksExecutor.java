package com.juancpaz.ecom.tools.command.healthcheck.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;
import com.juancpaz.ecom.tools.command.healthcheck.HealthCheckType;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthChecks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HealthChecksExecutor {
	@Autowired 
	private PingHealthCheckExecutor pingHealthCheckExecutor;
	@Autowired 
	private RestHealthCheckExecutor restHealthCheckExecutor;
	
	@Data
	private static class CallableExecutor implements Callable<Boolean> {
		private HealthCheckExecutor healthCheckExecutor;
		private HealthCheck healthCheck;
		
		@Override
		public Boolean call() throws Exception {
			Boolean result = healthCheckExecutor.execute(healthCheck);
			return result;
		}
	}

	public Boolean execute(HealthChecks healthChecks) throws HealthCheckException {
		List<CallableExecutor> callableExecutors = new ArrayList<CallableExecutor>();
		for (HealthCheck healthCheck: healthChecks.getHealthChecks()) {
			CallableExecutor callableExecutor = new CallableExecutor();
			callableExecutor.setHealthCheck(healthCheck);
			if (healthCheck.getType() == HealthCheckType.PING) {
				callableExecutor.setHealthCheckExecutor(pingHealthCheckExecutor);
			} else if (healthCheck.getType() == HealthCheckType.REST) {
				callableExecutor.setHealthCheckExecutor(restHealthCheckExecutor);
			}
			callableExecutors.add(callableExecutor);
		}
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
		try {
			log.info("Enter execute healthcheks");			
			Integer timeout = healthChecks.getTimeout();
			if (timeout != null) {
				results = executorService.invokeAll(callableExecutors, timeout, TimeUnit.SECONDS);
			} else {
				for (CallableExecutor callableExecutor: callableExecutors) {
					results.add(executorService.submit(callableExecutor));
				}
			}

			int i = 0;
			for (Future<Boolean> result : results) {
				if (!result.get()) {
					log.error("Health check " + healthChecks.getHealthChecks().get(i).getName() + " result: ERROR");
					return false;
				}
				log.info("Health check " + healthChecks.getHealthChecks().get(i).getName() + " result: SUCCESS");
				i++;
			}
			return true;
		} catch (Exception e) {
			throw new HealthCheckException(e);
		} finally {
			executorService.shutdownNow();
			log.info("Leave execute healthcheks");			
		}
	}
}
