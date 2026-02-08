package com.github.mael2001.config.sms;

import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VonageConf implements SMSConfig {

	private String apiUrl;
	private String apiKey;
	private String apiSecret;
	private String applicationId;
	private String brandName;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}

	@Override
	public String getProviderName() {
		return "Vonage";
	}

}
