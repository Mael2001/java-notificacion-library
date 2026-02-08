package com.github.mael2001.config.sms;

import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class VonageConf implements SMSConfig {

	@Getter
	@Setter
	private String apiUrl;

	@Getter
	@Setter
	private String apiKey;

	@Getter
	@Setter
	private String apiSecret;

	@Getter
	@Setter
	private String applicationId;

	@Getter
	@Setter
	private String brandName;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}

	@Override
	public String getProviderName() {
		return "Vonage";
	}

	public VonageConf() {

	}

	public VonageConf(String apiUrl, String apiKey, String apiSecret, String appId, String brandName){
		this.apiUrl = apiUrl;
		this.apiKey = apiKey;
		this.applicationId = appId;
		this.apiSecret = apiSecret;
		this.brandName = brandName;
	}
}
