package com.github.mael2001.exceptions;

public class RabbitException extends Exception {

	public RabbitException(String message, Throwable cause) {
		super(message, cause);
	}

	public RabbitException(Throwable cause) {
		super(cause);
	}

}
