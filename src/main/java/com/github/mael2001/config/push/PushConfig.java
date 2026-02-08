package com.github.mael2001.config.push;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class PushConfig implements ProviderConfig {
	@Getter
	@Setter
	private String apiKey;
	@Getter
	@Setter
	private String apiUrl;
	@Getter
	@Setter
	private String providerName;

	public PushConfig() {
		// Default constructor
	}

	public PushConfig(String apiKey, String apiUrl, String providerName) {
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.providerName = providerName;
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}
}
