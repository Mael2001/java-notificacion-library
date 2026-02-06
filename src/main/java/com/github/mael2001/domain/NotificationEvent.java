package com.github.mael2001.domain;

import java.time.Instant;

import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class NotificationEvent {

	@Getter @Setter private String id;
	@Getter @Setter private NotificationChannel channel;
	@Getter @Setter private String provider;
	@Getter @Setter private NotificationResult result;
	@Getter @Setter private Instant occurredAt;

	public NotificationEvent(String id, String provider, NotificationChannel channel, Instant occurredAt, NotificationResult result) {
		this.id = id;
		this.provider = provider;
		this.channel = channel;
		this.occurredAt = occurredAt;
		this.result = result;
	}

}
