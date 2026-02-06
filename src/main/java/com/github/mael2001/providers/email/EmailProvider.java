package com.github.mael2001.providers.email;

import com.github.mael2001.channels.email.EmailNotification;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.dto.Notifier;

public interface EmailProvider extends Notifier<EmailNotification> {

	EmailConfig getEmailConfig();

	RetryConfig getRetryConfig();

	String getName();

	String getProviderType();

}
