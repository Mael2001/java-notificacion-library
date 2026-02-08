package com.github.mael2001.config.rabbit;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class RabbitMQConfig implements ProviderConfig {
	@Getter
	@Setter
	private String host;
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
	private String virtualHost;
	@Getter
	@Setter
	private String exchange;
	@Getter
	@Setter
	private String routingKey;
	@Getter
	@Setter
	private boolean durable;
	@Getter
	@Setter
	private boolean publisherConfirms;
	@Getter
	@Setter
	private String providerName;

	public RabbitMQConfig() {
		// Default constructor
	}

	public RabbitMQConfig(String host, int port, String username, String password, String virtualHost, String exchange,
			String routingKey, boolean durable, boolean publisherConfirms, String providerName) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.virtualHost = virtualHost;
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.durable = durable;
		this.publisherConfirms = publisherConfirms;
		this.providerName = providerName;
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.RABBITMQ;
	}
}
