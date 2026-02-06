package com.github.mael2001.client;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;

import java.util.concurrent.CompletableFuture;

public interface NotificationClient {

    NotificationResult send(NotificationRequest request);

    default CompletableFuture<NotificationResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request));
    }

}
