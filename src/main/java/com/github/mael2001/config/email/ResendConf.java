package com.github.mael2001.config.email;

import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResendConf  implements EmailConfig {

	private String from;
	private String apiKey;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public String getProviderName() {
		return "Resend";
	}

}
