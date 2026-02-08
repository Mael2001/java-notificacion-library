package com.github.mael2001.spi;

import java.util.concurrent.CompletableFuture;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;

public interface Notifier<T extends NotificationRequest> extends Service {

    NotificationChannel channel();

    NotificationResult send(T request);

    default NotificationResult sendAsync(T request) {
		// Implement retry logic for async sending
		CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {
			return this.send(request);
		}).exceptionally(ex -> {
			return NotificationResult.failure(this.getName(), ErrorTypes.UNKNOWN, ex.getMessage(), channel());
		});
		return future.join();
    }

    String getProviderType();
}