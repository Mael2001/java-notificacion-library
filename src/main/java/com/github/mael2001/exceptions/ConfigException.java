package com.github.mael2001.exceptions;

import javax.naming.ConfigurationException;

public class ConfigException extends ConfigurationException {

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}

	public ConfigException(Throwable cause) {
		super(cause.getMessage());
		initCause(cause);
	}

}
