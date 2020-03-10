package com.juancpaz.ecom.tools.command;

import org.springframework.stereotype.Service;

import com.juancpaz.ecom.tools.command.exceptions.CommandException;

@Service
public class CommandExecutor {

	public void execute(Command<?> command) throws CommandException {
		command.execute();
	}
}
