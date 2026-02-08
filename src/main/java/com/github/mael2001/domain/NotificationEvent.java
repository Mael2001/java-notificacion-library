package com.github.mael2001.domain;

import java.time.Instant;

import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationEvent {

	private String id;
	private NotificationChannel channel;
	private String provider;
	private NotificationResult result;
	private Instant occurredAt;

}
