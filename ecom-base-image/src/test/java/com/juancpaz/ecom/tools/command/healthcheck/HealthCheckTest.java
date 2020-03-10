package com.juancpaz.ecom.tools.command.healthcheck;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.junit4.SpringRunner;

import com.juancpaz.ecom.tools.CommandLineTool;
import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;
import com.juancpaz.ecom.tools.command.exceptions.InvalidOptionException;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@Slf4j
public class HealthCheckTest {
	
	private SpringApplication application;
	
	@Rule
	public TestWatcher watchman = new TestWatcher() {
		protected void starting(Description description) {
			log.info("Starting test: " + description.getMethodName());
		}
	};

	@Before
	public void before() {
		application = new SpringApplication(CommandLineTool.class);
	}
	
	@Test
	public void shouldRunSuccessHealthCheck() {
		application.run(new String[] {"health-check"});
 	}

	@Test(expected = InvalidOptionException.class)
	public void shouldCatchBadArguments() throws Throwable {
		try {
			application.run(new String[] {"health-check", "-k", "health-check-filenotfound.yml"});
		} catch(IllegalStateException e) {
			throw e.getCause();
		}
 	}	
	
	@Test(expected = HealthCheckException.class)
	public void shouldCatchFileNotFound() throws Throwable {
		try {
			application.run(new String[] {"health-check", "-f", "health-check-filenotfound.yml"});
		} catch(IllegalStateException e) {
			throw e.getCause();
		}
 	}
	
	@Test(expected = HealthCheckException.class)
	public void shouldCatchTimeout() throws Throwable {
		try {
			application.run(new String[] {"health-check", "-f", "timeouts.yml"});
		} catch(IllegalStateException e) {
			throw e.getCause();
		}
 	}
	
}
