package com.github.mael2001.config.email;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class EmailConfig implements ProviderConfig {
	@Getter
	@Setter
	private String smtpServer;
	@Getter
	@Setter
	private int port;
	@Getter
	@Setter
	private String username;
	@Getter
	@Setter
	private String password;
	@Getter
	@Setter
	private boolean useSSL;
	@Getter
	@Setter
	private boolean useTLS;
	@Getter
	@Setter
	private String fromAddress;
	@Getter
	@Setter
	private String providerName;

	public EmailConfig() {
		// Default constructor
	}

	public EmailConfig(String smtpServer, int port, String username, String password, boolean useSSL, boolean useTLS,
			String fromAddress, String providerName) {
		this.smtpServer = smtpServer;
		this.port = port;
		this.username = username;
		this.password = password;
		this.useSSL = useSSL;
		this.useTLS = useTLS;
		this.fromAddress = fromAddress;
		this.providerName = providerName;
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}
}
