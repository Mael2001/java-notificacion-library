package com.github.mael2001.channels;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class EmailNotification implements NotificationRequest {

	@Getter @Setter private String recipient;
	@Getter @Setter private String subject;
	@Getter @Setter private String body;

	@Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

	public EmailNotification() {
		// Default constructor
	}

	public EmailNotification(String recipient, String subject, String body) {
		this.recipient = recipient;
		this.subject = subject;
		this.body = body;
	}

}
