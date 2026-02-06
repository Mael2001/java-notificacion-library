package com.github.mael2001.domain;

import java.time.Instant;

import com.github.mael2001.dto.NotificationChannel;

public interface NotificationRequest {

    NotificationChannel channel();

    default String correlationId() {
        return null;
    }

    default Instant createdAt() {
        return Instant.now();
    }

    default void validate() {}
}
