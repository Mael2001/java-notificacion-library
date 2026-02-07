package com.github.mael2001.domain;

import java.time.Instant;

import com.github.mael2001.dto.NotificationChannel;

public interface NotificationRequest {

    NotificationChannel channel();

    default String correlationId() {
        //Generate a radnom UUID or return null if not needed
        return java.util.UUID.randomUUID().toString();
    }

    default Instant createdAt() {
        return Instant.now();
    }

}
