package com.github.mael2001.config.email;

import com.github.mael2001.config.ProviderConfig;

public interface EmailConfig extends ProviderConfig {
	String getFrom();
}