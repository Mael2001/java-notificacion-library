package com.github.mael2001.config;

import lombok.Getter;
import lombok.Setter;

public class RetryConfig {
	@Getter @Setter private int maxRetries;
	@Getter @Setter private int retryIntervalMillis;
}
