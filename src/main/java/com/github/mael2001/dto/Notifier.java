package com.github.mael2001.dto;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;

public interface Notifier<T extends NotificationRequest> {

    NotificationChannel channel();

    NotificationResult send(T request);
}