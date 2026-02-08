package com.github.mael2001.channels;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
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
	@Getter
	@Setter
	private boolean async;
	@Setter
	private NotificationChannel channel;

	public EmailNotification(String[] recipients, String subject, String body, String providerName, boolean async) {
		this.providerName = providerName;
		this.recipients = recipients;
		this.cc = null;
		this.bcc = null;
		this.subject = subject;
		this.body = body;
		this.async = async;
	}

	public EmailNotification(String[] recipients, String[] cc, String[] bcc, String subject, String body,
			String providerName, boolean async) {
		this.providerName = providerName;
		this.recipients = recipients;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
		this.async = async;
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.EMAIL;
	}

}
