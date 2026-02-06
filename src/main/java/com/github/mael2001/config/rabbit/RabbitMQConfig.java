package com.github.mael2001.config.rabbit;

import lombok.Getter;
import lombok.Setter;

public class RabbitMQConfig {
	@Getter @Setter private String host;
	@Getter @Setter private int port;
	@Getter @Setter private String username;
	@Getter @Setter private String password;
	@Getter @Setter private String virtualHost;
	@Getter @Setter private String exchange;
	@Getter @Setter private String routingKey;
	@Getter @Setter private boolean durable;
	@Getter @Setter private boolean publisherConfirms;

}
