package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
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
	@Getter
	@Setter
	private boolean async;
	@Setter
	private NotificationChannel channel;


	public SMSNotification(String message, String phoneNumber, String providerName, boolean async) {
		this.message = message;
		this.phoneNumber = phoneNumber;
		this.providerName = providerName;
		this.async = async;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.SMS;
	}
}
