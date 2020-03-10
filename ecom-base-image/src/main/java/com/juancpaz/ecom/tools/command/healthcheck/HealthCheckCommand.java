package com.juancpaz.ecom.tools.command.healthcheck;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.juancpaz.ecom.tools.command.Command;
import com.juancpaz.ecom.tools.command.exceptions.CommandException;
import com.juancpaz.ecom.tools.command.exceptions.HealthCheckException;
import com.juancpaz.ecom.tools.command.exceptions.InvalidOptionException;
import com.juancpaz.ecom.tools.command.healthcheck.executor.HealthChecksExecutor;
import com.juancpaz.ecom.tools.command.healthcheck.model.HealthChecks;

@Component
public class HealthCheckCommand<T> extends Command<Boolean> {
	private static enum ParseState {
		NONE, GET_OPT, GET_FILENAME;
	}
	private static enum Format {
		NONE, YAML, JSON, PROPS;
	}
	
	@Autowired
	private HealthChecksExecutor healthChecksExecutor;	
	
	private List<String> filenames = new ArrayList<String>();
	private HealthChecks healthChecks = null;
	
	public void parseCommandLine(String[] args) throws CommandException {
		ParseState parseState = ParseState.GET_OPT;
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				continue;
			}
			if (parseState == ParseState.GET_OPT) {
				if (args[i].equals("-f")) {
					parseState = ParseState.GET_FILENAME;
				}
			} else if (parseState == ParseState.GET_FILENAME) {
				filenames.add(args[i]);
				parseState = ParseState.GET_OPT;
			} else {
				throw new InvalidOptionException("Invalid option for health-check command: " + args[i]);
			}
		}
		if (filenames.isEmpty()) {
			filenames.add("health-check.yml");
			filenames.add("health-check.json");
			filenames.add("health-check.properties");
		}
		String content = null;
		Format format = Format.NONE; 
		for (String filename : filenames) {
			if (filename.endsWith(".yml")) {
				format = Format.YAML;
			}
			Path path = Paths.get(filename);
			if (path.isAbsolute()) {
				try {
					content = new String(Files.readAllBytes(path));
				} catch (IOException e) {
					throw new HealthCheckException("Cannot load file: " + filename);
				}
				continue;
			} 
			try (InputStream is = HealthCheckCommand.class.getClassLoader().getResourceAsStream(filename)) {
				content = IOUtils.toString(is, Charset.defaultCharset());
				break;
			} catch (Exception e) {
				throw new HealthCheckException("Cannot load file: " + filename);
			}
			
		}
		if (content == null) {
			throw new HealthCheckException("Nothing to do");
		}
		
		healthChecks = null;
		ObjectMapper objectMapper = null;
		try {
			if (format == Format.YAML) {
				objectMapper = new ObjectMapper(new YAMLFactory());
			}
			healthChecks = (HealthChecks) objectMapper.readValue(content, HealthChecks.class);
		} catch (Exception e) {
			throw new HealthCheckException(e);
		}		
		
	}
	
	@Override
	public Boolean execute() throws HealthCheckException {
		return healthChecksExecutor.execute(healthChecks);
	}
}
