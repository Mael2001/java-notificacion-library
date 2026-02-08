package com.github.mael2001.providers.email;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import io.mailtrap.model.response.emails.SendResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailtrapEmailProvider implements EmailProvider {

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
	private MailtrapConf providerConfig;

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
		if (config instanceof MailtrapConf mailtrapConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = mailtrapConfig;
		} else {
			throw new ValidationException("Invalid provider config type for MailtrapEmailProvider");
		}
	}

	@Override
	public NotificationResult send(EmailNotification request) {

		// Implement retry logic for async sending
		NotificationResult finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
				"notification wasn't triggered");

		log.info("Triggering {}, with {} provider", this.getProviderType().toString(), this.getName());
		// Retry loop
		for (int attempt = 1; attempt <= retryConfig.getMaxRetries(); attempt++) {
			log.info("Attempt {} to send email via {}", attempt, this.getName());
			try {
				// Send email
				SendResponse response = sendEmail(request);
				// Check response for success
				log.info("Received response from {}: {}", this.getName(), response);
				if (response.isSuccess()) {
					log.info("Email sent successfully via {} on attempt {}", this.getName(), attempt);
					return NotificationResult.success(this.getName(), channel(), "Email sent successfully");
				} else {
					log.error("Attempt {} failed: {}", attempt,
							response.getMessageIds().stream().collect(Collectors.joining(",")));
					finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
							response.getMessageIds().toString(), channel());
				}
				// Wait before next retry
				TimeUnit.MILLISECONDS.sleep(retryConfig.getRetryIntervalMillis());
			} catch (Exception ex) {
				if (ex.getMessage().contains("Unauthorized") || ex.getMessage().contains("Too many failed login attempts")) {
					return NotificationResult.failure(this.name, ErrorTypes.CONFIGURATION, name, channel());
				}
				log.error("Attempt {} threw an exception: {}", attempt, ex.getMessage());
				finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER, ex.getMessage(), channel());
			}
		}
		return finalResult;
	}

	@Override
	public NotificationResult sendAsync(EmailNotification request) {
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

	// This method is used to send the email without retry logic, it is called by
	// the async method
	private SendResponse sendEmail(EmailNotification request) {
		// Separate Provider Configurations
		MailtrapConf mailtrapConfig = this.getProviderConfig();

		// Create Mailtrap Configurations
		final MailtrapConfig config = new MailtrapConfig.Builder()
				.token(mailtrapConfig.getApiToken())
				.sandbox(mailtrapConfig.isSandbox())
				.inboxId(mailtrapConfig.getInboxId())
				.connectionTimeout(Duration.ofSeconds(globalConfig.getConnectionTimeout()))
				.build();

		// Create Mailtrap Client
		final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

		// Create list of addresses from recipients
		List<Address> toAddresses = request.getRecipients() != null
				? List.of(request.getRecipients()).stream().map(Address::new).toList()
				: List.of();

		// Create list of cc addresses from ccRecipients
		List<Address> ccAddresses = request.getCc() != null
				? List.of(request.getCc()).stream().map(Address::new).toList()
				: List.of();

		// Create list of bcc addresses from bccRecipients
		List<Address> bccAddresses = request.getBcc() != null
				? List.of(request.getBcc()).stream().map(Address::new).toList()
				: List.of();

		// Build the email
		final MailtrapMail mail = MailtrapMail.builder()
				.from(new Address(mailtrapConfig.getFrom()))
				.to(toAddresses)
				.cc(ccAddresses)
				.bcc(bccAddresses)
				.subject(request.getSubject())
				.text(request.getBody())
				.build();

		return client.send(mail);
	}

}
