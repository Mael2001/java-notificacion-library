package com.github.mael2001.config.sms;

import com.github.mael2001.config.ProviderConfig;

public interface SMSConfig extends ProviderConfig {

	String getApiKey();

	String getApiUrl();
}