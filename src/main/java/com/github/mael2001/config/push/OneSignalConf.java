package com.github.mael2001.config.push;

import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class OneSignalConf implements PushConfig {

	@Getter
	@Setter
	private String apiUrl;

	@Getter
	@Setter
	private String apiKey;

	@Getter
	@Setter
	private String appId;

	@Getter
	@Setter
	private boolean debug;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public String getProviderName() {
		return "Onesignal";
	}

	public OneSignalConf(){

	}

	public OneSignalConf(String apiUrl, String apiKey, String appId, boolean debug){
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.appId = appId;
		this.debug = debug;
	}

}
