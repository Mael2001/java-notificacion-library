package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class SMSNotification implements NotificationRequest {
	@Getter
	@Setter
	private String message;
	@Getter
	@Setter
	private String phoneNumber;
	@Getter
	@Setter
	private String providerName;
	@Setter
	private NotificationChannel channel;

	public SMSNotification() {
		// Default constructor
	}
	
	public SMSNotification(String message, String phoneNumber, String providerName) {
		this.message = message;
		this.phoneNumber = phoneNumber;
		this.providerName = providerName;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.SMS;
	}
}
