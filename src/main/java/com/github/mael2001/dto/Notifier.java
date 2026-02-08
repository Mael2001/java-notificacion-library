package com.github.mael2001.dto;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;

public interface Notifier<T extends NotificationRequest> extends Service {

    NotificationChannel channel();

    NotificationResult send(T request);

    NotificationResult sendAsync(T request);

	String getProviderType();
}