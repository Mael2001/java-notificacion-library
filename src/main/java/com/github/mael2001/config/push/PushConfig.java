package com.github.mael2001.config.push;

import com.github.mael2001.config.ProviderConfig;

public interface PushConfig extends  ProviderConfig {

	String getApiKey();

	String getApiUrl();

}
