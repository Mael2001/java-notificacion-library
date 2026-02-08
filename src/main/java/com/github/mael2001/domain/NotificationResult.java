package com.github.mael2001.domain;

import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Data;

@Data
public class NotificationResult {

	private boolean success;
	private String provider;
	private NotificationChannel channel;
	private String message;
	private ErrorTypes errorType;
	private String errorMessage;

	public static NotificationResult success(String provider, NotificationChannel channel, String message) {
		NotificationResult result = new NotificationResult();
		result.setSuccess(true);
		result.setProvider(provider);
		result.setChannel(channel);
		result.setMessage(message);
		return result;
	}

	public static NotificationResult failure(String provider, ErrorTypes errorType, String errorMessage) {
		NotificationResult result = new NotificationResult();
		result.setSuccess(false);
		result.setProvider(provider);
		result.setErrorType(errorType);
		result.setErrorMessage(errorMessage);
		return result;
	}

	public static NotificationResult failure(String provider, ErrorTypes errorType, String errorMessage, NotificationChannel channel) {
		NotificationResult result = new NotificationResult();
		result.setSuccess(false);
		result.setProvider(provider);
		result.setChannel(channel);
		result.setErrorType(errorType);
		result.setErrorMessage(errorMessage);
		return result;
	}

}
