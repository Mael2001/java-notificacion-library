package com.github.mael2001.config.email;

import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class ResendConf  implements EmailConfig {

	@Getter
	@Setter
	private String from;

	@Getter
	@Setter
	private String apiKey;


	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public String getProviderName() {
		return "Resend";
	}

	public ResendConf(){

	}

	public ResendConf(String from, String apiKey){
		this.from = from;
		this.apiKey = apiKey;
	}

}
