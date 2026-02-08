package com.github.mael2001.config;

import com.github.mael2001.dto.NotificationChannel;

public interface ProviderConfig {

    NotificationChannel channel();

	String getProviderName();
}