package com.juancpaz.ecom.tools.command.exceptions;

@SuppressWarnings("serial")
public class CommandException extends Exception {
	public CommandException() {
		super();
	}

	public CommandException(String message) {
		super(message);
	}

	public CommandException(Exception e) {
		super(e);
	}
}
