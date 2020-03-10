package com.juancpaz.ecom.tools.command;

import com.juancpaz.ecom.tools.command.exceptions.CommandException;

public abstract class Command<T> {

	public abstract void parseCommandLine(String[] args) throws CommandException;
	public abstract T execute() throws CommandException;

}
