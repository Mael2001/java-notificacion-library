package com.github.mael2001.models;

import java.util.ArrayList;
import java.util.List;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.push.PushConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.providers.push.PushProvider;

import lombok.Getter;
import lombok.Setter;

public class FakePushProvider implements PushProvider {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	private PushConfig providerConfig;
	private List<PushNotification> sent = new ArrayList<>();

	@Getter
	@Setter
	private String name;

	public FakePushProvider() {
	}

	@Override
	public String getProviderType() {
		return NotificationChannel.PUSH.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public NotificationResult send(PushNotification request) {
		sent.add(request);

		return NotificationResult.success(
				"in-memory-push",
				NotificationChannel.PUSH,
				"in-memory-push-sent");
	}

	// test helper
	public List<PushNotification> sentPushNotifications() {
		return List.copyOf(sent);
	}

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof PushConfig pushConfig) {
			this.providerConfig = pushConfig;
		} else {
			throw new ValidationException("Invalid provider config type for FakePushProvider");
		}
	}
}

