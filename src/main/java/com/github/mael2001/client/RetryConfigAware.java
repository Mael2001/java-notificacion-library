package com.github.mael2001.client;

import com.github.mael2001.config.RetryConfig;

public interface RetryConfigAware {
	void setRetryConfig(RetryConfig config);
}
