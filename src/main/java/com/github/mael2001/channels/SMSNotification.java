package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class SMSNotification implements NotificationRequest {
	@Getter @Setter private String message;
	@Getter @Setter private String phoneNumber;

	@Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }
}
