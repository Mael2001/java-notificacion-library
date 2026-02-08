package com.github.mael2001.config.rabbit;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.dto.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQConfig implements ProviderConfig {
	private String host;
	private int port;
	private String username;
	private String password;
	private String virtualHost;
	private String exchange;
	private String routingKey;
	private boolean durable;
	private boolean publisherConfirms;
	private String providerName;

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.RABBITMQ;
	}
}
