package com.github.mael2001.exceptions;

import java.util.concurrent.ExecutionException;

public class ProviderException extends ExecutionException {

	public ProviderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderException(Throwable cause) {
		super(cause);
	}
}
