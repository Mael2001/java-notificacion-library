package com.github.mael2001.channels.email;
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

}
