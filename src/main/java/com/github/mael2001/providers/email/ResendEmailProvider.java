package com.github.mael2001.providers.email;

import java.util.concurrent.CompletableFuture;
import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.ResendConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResendEmailProvider implements EmailProvider {

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
	private ResendConf providerConfig;

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
		if (config instanceof ResendConf emailConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = emailConfig;
		} else {
			throw new ValidationException("Invalid provider config type for ResendEmailProvider");
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
				CreateEmailResponse response = sendEmail(request);
				// Check response for success
				log.info("Email sent successfully via {} on attempt {}", this.getName(), attempt);
				return NotificationResult.success(this.getName(), channel(),
						"Email sent successfully id: " + response.getId());
				// Wait before next retry
			} catch (Exception ex) {
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

	private CreateEmailResponse sendEmail(EmailNotification request) throws ResendException {

		// Get provider config
		ResendConf config = this.getProviderConfig();

		// Create Resend Client
		Resend resend = new Resend(config.getApiKey());


		// Log email details (avoid logging sensitive info in production)
		log.info("Preparing to send email via {}: to={}, cc={}, bcc={}, subject={}",
				this.getName(),
				String.join(",", request.getRecipients()),
				request.getCc() != null ? String.join(",", request.getCc()) : "none",
				request.getBcc() != null ? String.join(",", request.getBcc()) : "none",
				request.getSubject()
		);

		// Build email parameters
		CreateEmailOptions params = CreateEmailOptions.builder()
				.from(config.getFrom())
				.to(request.getRecipients())
				.cc(request.getCc())
				.bcc(request.getBcc())
				.subject(request.getSubject())
				.html(request.getBody())
				.build();

		// Send email using Resend client
		return resend.emails().send(params);
	}
}
