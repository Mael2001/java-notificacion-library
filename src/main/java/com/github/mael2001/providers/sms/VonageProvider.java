package com.github.mael2001.providers.sms;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.sms.VonageConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VonageProvider implements SMSProvider {

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
	private VonageConf providerConfig;

	@Override
	public String getProviderType() {
		return NotificationChannel.SMS.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof VonageConf smsConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = smsConfig;
		} else {
			throw new ValidationException("Invalid provider config type for TwilioProvider");
		}
	}

	@Override
	public NotificationResult send(SMSNotification request) {
		// Implement retry logic for async sending
		NotificationResult finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
				"notification wasn't triggered");

		log.info("Triggering {}, with {} provider", this.getProviderType().toString(), this.getName());
		// Retry loop
		for (int attempt = 1; attempt <= retryConfig.getMaxRetries(); attempt++) {
			log.info("Attempt {} to send email via {}", attempt, this.getName());
			try {
				// Send email
				SmsSubmissionResponse response = sendSMS(request);
				// Check response for success
				log.info("Received response from {}: {}", this.getName(), response);
				if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
					log.info("Push sent successfully via {} on attempt {}", this.getName(), attempt);
					return NotificationResult.success(this.getName(), channel(), "Push sent successfully");
				} else {
					log.error("Attempt {} failed: id: {} errors: {}", attempt,
							response.getMessageCount(), response.getMessages());
					finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
							response.getMessages().stream().map(n -> n.getErrorText()).collect(Collectors.joining(","))
									.toString(),
							channel());
				}
				// Wait before next retry
				TimeUnit.MILLISECONDS.sleep(retryConfig.getRetryIntervalMillis());
			} catch (Exception ex) {
				log.error("Attempt {} threw an exception: {}", attempt, ex.getMessage());
				finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER, ex.getMessage(), channel());
			}
		}
		return finalResult;
	}

	@Override
	public NotificationResult sendAsync(SMSNotification request) {
		// Implement retry logic for async sending
		log.info("Triggering {}, with {} provider", this.getProviderType().toString(), this.getName());

		CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {
			return this.send(request);
		}).exceptionally(ex -> {
			log.error("Failed to send email async via {}: {}", this.getName(), ex.getMessage());
			return NotificationResult.failure(this.getName(), ErrorTypes.UNKNOWN, ex.getMessage(), channel());
		});
		return future.join();
	}

	private SmsSubmissionResponse sendSMS(SMSNotification request) {

		log.info("Starting SMS Send process");
		// Separate Provider Config
		VonageConf providerConf = this.getProviderConfig();

		log.info("Creating {} client", this.getName());
		// Create Vonage client
		VonageClient client = VonageClient.builder()
				.apiKey(providerConf.getApiKey())
				.apiSecret(providerConf.getApiSecret())
				.applicationId(providerConf.getApplicationId())
				.build();

		log.info("{} client created", this.getName());

		log.info("Sending message with {}", this.getName());
		TextMessage message = new TextMessage(
				providerConf.getBrandName(),
				request.getPhoneNumber(),
				request.getMessage());
		log.info("Message sent with {}", this.getName());

		log.info("Returning response");
		return client.getSmsClient().submitMessage(message);
	}

}
