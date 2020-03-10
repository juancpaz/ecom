package com.juancpaz.ecom.tools.command;

public enum CommandType {	
	HEALTH_CHECK("health-check");
	
	private String name;	

	CommandType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
