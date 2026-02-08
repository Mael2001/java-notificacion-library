package com.github.mael2001.config.email;

import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailtrapConf implements EmailConfig {

	private String from;
	private String apiToken;
	private boolean sandbox;
	private long inboxId;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public String getProviderName() {
		return "Mailtrap";
	}

}
