package com.github.mael2001.providers.push;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.push.PushConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneSignalProvider implements PushProvider {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	@Setter
	private String name;
	@Getter
	private PushConfig providerConfig;

	@Override
	public String getProviderType() {
		return NotificationChannel.PUSH.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof PushConfig pushConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = pushConfig;
		} else {
			throw new ValidationException("Invalid provider config type for OneSignalProvider");
		}
	}

	@Override
	public NotificationResult send(PushNotification request) {
		// Check if async is enabled in global config
		if (globalConfig.isEnableAsync()) {
			// Implement retry logic for async sending
			NotificationResult finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
					"notification wasn't triggered");

			// Retry loop
			for (int attempt = 1; attempt <= retryConfig.getMaxRetries(); attempt++) {
				try {
					finalResult = sendAsync(request);
					if (finalResult.isSuccess()) {
						return finalResult;
					} else {
						log.error("Attempt {} failed: {}", attempt, finalResult.getErrorMessage());
					}
				} catch (Exception ex) {
					log.error("Attempt {} threw an exception: {}", attempt, ex.getMessage());
				}

				// Wait before next retry
				try {
					TimeUnit.MILLISECONDS.sleep(retryConfig.getRetryIntervalMillis());
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return NotificationResult.failure(this.getName(), null, "Retry interrupted", channel());
				}
			}
			return finalResult;
		}

		log.info("Push notification sent successfully via {} to {}", this.getName(), request.getRecipientDeviceToken());
		return NotificationResult.success(this.getName(), channel(), "Push notification sent successfully");
	}

	private NotificationResult sendAsync(PushNotification request) {
		CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {

			log.info("Push notification sent successfully via {} to {}", this.getName(), request.getRecipientDeviceToken());
			return NotificationResult.success(this.getName(), channel(), "Push notification sent successfully");
		}).orTimeout(globalConfig.getConnectionTimeout(), TimeUnit.SECONDS)
				.exceptionally(ex -> {
					log.error("Failed to send push notification via {}: {}", this.getName(), ex.getMessage());
					return NotificationResult.failure(this.getName(), null, ex.getMessage(), channel());
				});
		return future.join();
	}
}
