package com.github.mael2001.config.email;

import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class MailtrapConf implements EmailConfig {

	@Getter
	@Setter
	private String from;

	@Getter
	@Setter
	private String apiToken;

	@Getter
	@Setter
	private boolean sandbox;

	@Getter
	@Setter
	private long inboxId;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public String getProviderName() {
		return "Mailtrap";
	}

	public MailtrapConf() {
		// Default constructor
	}

	public MailtrapConf(String from, String apiToken, boolean sandbox, long inboxId) {
		this.from = from;
		this.apiToken = apiToken;
		this.sandbox = sandbox;
		this.inboxId = inboxId;
	}

}
