package com.github.mael2001.providers.email;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendGridEmailProvider implements EmailProvider {

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
	private EmailConfig providerConfig;

	@Override
	public String getProviderType() {
		return NotificationChannel.EMAIL.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof EmailConfig emailConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = emailConfig;
		} else {
			throw new ValidationException("Invalid provider config type for SendGridEmailProvider");
		}
	}

	@Override
	public NotificationResult send(EmailNotification request) {
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

		log.info("Email sent successfully via {} to {}", this.getName(), request.getRecipient());
		return NotificationResult.success(this.getName(), channel(), "Email sent successfully");
	}

	private NotificationResult sendAsync(EmailNotification request) {
		CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {

			log.info("Email sent successfully via {} to {}", this.getName(), request.getRecipient());
			return NotificationResult.success(this.getName(), channel(), "Email sent successfully");
		}).orTimeout(globalConfig.getConnectionTimeout(), TimeUnit.SECONDS)
				.exceptionally(ex -> {
					log.error("Failed to send email via {}: {}", this.getName(), ex.getMessage());
					return NotificationResult.failure(this.getName(), null, ex.getMessage(), channel());
				});
		return future.join();
	}

}
