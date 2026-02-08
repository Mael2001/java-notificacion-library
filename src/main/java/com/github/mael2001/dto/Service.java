package com.github.mael2001.dto;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;

public interface Service {

	ProviderConfig getProviderConfig();

	RetryConfig getRetryConfig();

	void setName(String name);

	String getName();
}
