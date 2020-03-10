package com.juancpaz.ecom.tools.command;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.juancpaz.ecom.tools.command.exceptions.CommandException;
import com.juancpaz.ecom.tools.command.exceptions.InvalidCommandException;
import com.juancpaz.ecom.tools.command.healthcheck.HealthCheckCommand;

@Component
public class CommandFactory {
    private final Map<String, Command<?>> commands = new HashMap<>();

    @Autowired
    private ApplicationContext context;
	
	public Command<?> getInstance(String[] args) throws CommandException {
		if (args.length == 0) {
			String validCommands = "[" + CommandType.HEALTH_CHECK.getName() + "]";
			throw new InvalidCommandException("InvalidCommand - Comnados validos = " + validCommands);
		}
		
		int i = 0;
		while(i < args.length && args[i].startsWith("--")) {
			i++;
		}
		Command<?> command = commands.get(args[i]);
		if (command != null) {
			return command;
		}
		
		throw new InvalidCommandException("InvalidCommand: " + args[0]);		
	}

    @PostConstruct
    public void initialize() {
        HealthCheckCommand<?> healthCheckCommand = context.getBean(HealthCheckCommand.class);
        commands.put(CommandType.HEALTH_CHECK.getName(), healthCheckCommand);
        
    }	
}
