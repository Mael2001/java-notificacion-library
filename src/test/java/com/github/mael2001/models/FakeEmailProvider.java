package com.github.mael2001.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.providers.email.EmailProvider;

import lombok.Getter;
import lombok.Setter;

public class FakeEmailProvider implements EmailProvider {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	private EmailConfig providerConfig;
	@Getter
	@Setter
	private String name;

    private List<EmailNotification> sent = new ArrayList<>();

	public FakeEmailProvider() {
	}

	@Override
	public String getProviderType() {
		return NotificationChannel.EMAIL.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.EMAIL;
	}
    @Override
    public NotificationResult send(EmailNotification request) {
        sent.add(request);

        return NotificationResult.success(
                "in-memory-email",
				NotificationChannel.EMAIL,
				"in-memory-email-sent"
        );
    }

    // test helper
    public List<EmailNotification> sentEmails() {
        return List.copyOf(sent);
    }

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof EmailConfig emailConfig) {
			this.providerConfig = emailConfig;
		} else {
			throw new ValidationException("Invalid provider config type for MailtrapEmailProvider");
		}
	}

	@Override
	public NotificationResult sendAsync(EmailNotification request) {
		return CompletableFuture.supplyAsync(() -> send(request)).join();
	}


}