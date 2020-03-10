package com.juancpaz.ecom.tools.command.healthcheck.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class HealthChecks {
	private String name;
	private String description;
	private Integer timeout;
	private List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();
	
	public String toJson() {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);			
		} catch (JsonProcessingException e) {
			return e.getMessage();
		}		
	}
	
	public String toString() {
		return toJson();
	}
}
