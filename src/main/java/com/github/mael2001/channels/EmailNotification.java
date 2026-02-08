package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class EmailNotification implements NotificationRequest {

	@Getter
	@Setter
	private String[] recipients;
	@Getter
	@Setter
	private String[] cc;
	@Getter
	@Setter
	private String[] bcc;
	@Getter
	@Setter
	private String subject;
	@Getter
	@Setter
	private String body;
	@Getter
	@Setter
	private String providerName;
	@Setter
	private NotificationChannel channel;


	public EmailNotification() {
		// Default constructor
	}

	public EmailNotification(String[] recipients, String subject, String body, String providerName) {
		this.providerName = providerName;
		this.recipients = recipients;
		this.cc = null;
		this.bcc = null;
		this.subject = subject;
		this.body = body;
	}

	public EmailNotification(String[] recipients, String[] cc, String[] bcc, String subject, String body, String providerName) {
		this.providerName = providerName;
		this.recipients = recipients;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.EMAIL;
	}
}
