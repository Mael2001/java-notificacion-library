package com.github.mael2001.config;

import lombok.Getter;
import lombok.Setter;

public class GlobalConfig {
	@Getter @Setter int connectionTimeout;
	@Getter @Setter int readTimeout;

	public GlobalConfig() {
		this.connectionTimeout = 30;
		this.readTimeout = 30;
	}

	public GlobalConfig(int connectionTimeout, int readTimeout) {
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}
}
