package com.github.mael2001.providers.email;

import java.util.concurrent.CompletableFuture;

import com.github.mael2001.channels.email.EmailNotification;
import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;

import lombok.Getter;
import lombok.Setter;

public class MailtrapEmailProvider implements EmailProvider, GlobalConfigAware {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	private final EmailConfig emailConfig;

	public MailtrapEmailProvider(EmailConfig emailConfig, RetryConfig retryConfig) {
		this.emailConfig = emailConfig;
		this.retryConfig = retryConfig;
	}

	@Override
	public String getName() {
		return "Mailtrap";
	}

	@Override
	public String getProviderType() {
		return "SMTP";
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}

	@Override
	public NotificationResult send(EmailNotification request) {
		// Check if async is enabled in global config
		if (globalConfig.isEnableAsync()) {
			CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {
				System.out.println("Sending email via Mailtrap to " + request.getRecipient());
				return NotificationResult.success(this.getName(), channel(), "Email sent successfully");
			})
					.exceptionally(ex -> {
						System.err.println("Failed to send email via Mailtrap: " + ex.getMessage());
						return NotificationResult.failure(this.getName(), null, ex.getMessage(), channel());
					});
			return future.join();
		}

		System.out.println("Sending email via Mailtrap to " + request.getRecipient());
		return NotificationResult.success(this.getName(), channel(), "Email sent successfully");
	}

}
