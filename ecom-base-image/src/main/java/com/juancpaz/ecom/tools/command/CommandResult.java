package com.juancpaz.ecom.tools.command;

import lombok.Data;

@Data
public class CommandResult {
	public enum CommandResultValue {
		SUCCESS(0), ERROR(1);		
		private int value;

		CommandResultValue(int value) {
			this.value = value;
		}
		
		int getValue() {
			return value;
		}
	}
	
	private CommandResultValue value = CommandResultValue.ERROR;
	private String message;
}
