package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class PushNotification implements NotificationRequest {
	@Getter
	@Setter
	private String title;
	@Getter
	@Setter
	private String message;
	@Getter
	@Setter
	private String recipientDeviceToken;
	@Getter
	@Setter
	private String providerName;
	@Setter
	private NotificationChannel channel;

	public PushNotification() {
		// Default constructor
	}

	public PushNotification(String title, String message, String recipientDeviceToken, String providerName) {
		this.providerName = providerName;
		this.title = title;
		this.message = message;
		this.recipientDeviceToken = recipientDeviceToken;
	}
	
	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.PUSH;
	}
}
