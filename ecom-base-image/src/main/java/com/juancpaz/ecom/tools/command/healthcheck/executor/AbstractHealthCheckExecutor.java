package com.juancpaz.ecom.tools.command.healthcheck.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthCheck;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractHealthCheckExecutor implements HealthCheckExecutor {

	@Override
	public Boolean execute(HealthCheck healthCheck) throws HealthCheckException {
		boolean done[] = new boolean[1];
		done[0] = false;
		if (Thread.interrupted()) {
			return false;
		}
		ExecutorService executorService = Executors.newSingleThreadExecutor();	
		Future<Boolean> future = null;
		try {
			future = executorService.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					log.info("Enter " + healthCheck.getName() + " healthcheck");

					boolean result = false;
					while (!done[0]) {
						try {
							result = executeHeathCheck(healthCheck);
							if (result) {
								done[0] = true;
							}
						} catch (Exception e) {
							result = false;
							if (Thread.interrupted()) {
								done[0] = true;
								throw e;
							}
						}
					}
					log.info("Exit " + healthCheck.getName() + " healthcheck");
					return result;
				}
			});

			return future.get(healthCheck.getTimeout(), TimeUnit.SECONDS);
		} catch (Exception e) {
			done[0] = true;
			future.cancel(true);
			throw new HealthCheckException(healthCheck, e);
		} finally {
			done[0] = true;
			executorService.shutdownNow();
		}
	}

	protected abstract Boolean executeHeathCheck(HealthCheck healthCheck) throws Exception;
}
