package com.github.mael2001.channels.push;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class PushNotification implements NotificationRequest {
	@Getter @Setter private String title;
	@Getter @Setter private String message;
	@Getter @Setter private String recipientDeviceToken;

	@Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }
}
