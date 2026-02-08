package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
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
	@Getter
	@Setter
	private boolean async;
	@Setter
	private NotificationChannel channel;

	public PushNotification(String title, String message, String recipientDeviceToken, String providerName, boolean async) {
		this.providerName = providerName;
		this.title = title;
		this.message = message;
		this.recipientDeviceToken = recipientDeviceToken;
		this.async = async;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.PUSH;
	}
}
