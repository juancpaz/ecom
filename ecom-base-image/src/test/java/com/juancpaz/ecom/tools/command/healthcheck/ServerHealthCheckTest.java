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

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties
@Slf4j
public class ServerHealthCheckTest {
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
	public void shouldRunSuccessHealthCheckOnDiscoveryServer() {
		application.run(new String[] {"health-check", "-f", "discovery-health-check.yml"});
 	}
	
	@Test
	public void shouldRunSuccessHealthCheckConfigurationServer() {
		application.run(new String[] {"health-check", "-f", "configuration-health-check.yml"});
 	}		
}
