package com.juancpaz.ecom.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.juancpaz.ecom.tools.command.Command;
import com.juancpaz.ecom.tools.command.CommandExecutor;
import com.juancpaz.ecom.tools.command.CommandFactory;
import com.juancpaz.ecom.tools.command.exceptions.CommandException;
import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CommandLineTool implements CommandLineRunner {
	@Autowired
	private ApplicationContext context;

	public static void main(String[] args) {
		log.info("ECOM Comandline Client start");
		try {
			SpringApplication application = new SpringApplication(CommandLineTool.class);
			application.setWebApplicationType(WebApplicationType.NONE);
			application.run(args);
			log.info("ECOM Command Client stop");
		} catch (Exception e) {
			String context = null;
			String message = e.getMessage();
			Throwable cause = e;
			while (cause != null && cause.getCause() != null) {
				if (cause instanceof HealthCheckException) {
					HealthCheckException hce = (HealthCheckException) cause;
					if (hce.getHealthCheck() != null && hce.getHealthCheck().getName() != null) {
						context = hce.getHealthCheck().getName();
					}
				}
				message = cause.getMessage();
				cause = cause.getCause();
			}
			if (context != null) {
				log.error(context + ": " + message);
			} else {
				message = cause.getMessage();
				log.error(message);
			}
			throw new RuntimeException(cause);
		}
	}

	@Override
	public void run(String... args) throws CommandException {
		CommandFactory commandFactory = context.getBean(CommandFactory.class);
		CommandExecutor commandExecutor = context.getBean(CommandExecutor.class);
		Command<?> command = commandFactory.getInstance(args);
		command.parseCommandLine(args);
		commandExecutor.execute(command);
	}
}
