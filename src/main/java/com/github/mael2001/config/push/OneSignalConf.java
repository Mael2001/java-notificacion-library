package com.github.mael2001.config.push;

import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneSignalConf implements PushConfig {

	private String apiUrl;
	private String apiKey;
	private String appId;
	private boolean debug;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public String getProviderName() {
		return "Onesignal";
	}

}
