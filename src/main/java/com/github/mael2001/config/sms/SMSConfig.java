package com.github.mael2001.config.sms;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class SMSConfig implements ProviderConfig {
	@Getter
	@Setter
	private String apiKey;
	@Getter
	@Setter
	private String apiUrl;
	@Getter
	@Setter
	private String providerName;

	public SMSConfig() {
		// Default constructor
	}

	public SMSConfig(String apiKey, String apiUrl, String providerName) {
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.providerName = providerName;
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}
}
