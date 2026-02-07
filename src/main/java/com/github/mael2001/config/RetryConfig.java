package com.github.mael2001.config;

import lombok.Getter;
import lombok.Setter;

public class RetryConfig {
	@Getter @Setter private int maxRetries;
	@Getter @Setter private int retryIntervalMillis;

	public RetryConfig() {
		this.maxRetries = 3;
		this.retryIntervalMillis = 1000;
	}

	public RetryConfig(int maxRetries, int retryIntervalMillis) {
		this.maxRetries = maxRetries;
		this.retryIntervalMillis = retryIntervalMillis;
	}
}
