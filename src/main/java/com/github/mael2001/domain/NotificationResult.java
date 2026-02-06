package com.github.mael2001.domain;

import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class NotificationResult {

	@Getter @Setter private boolean success;
	@Getter @Setter private String provider;
	@Getter @Setter private NotificationChannel channel;
	@Getter @Setter private String message;
	@Getter @Setter private ErrorTypes errorType;
	@Getter @Setter private String errorMessage;

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
