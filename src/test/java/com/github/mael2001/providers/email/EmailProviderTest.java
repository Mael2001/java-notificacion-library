package com.github.mael2001.providers.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.models.FakeEmailProvider;

public class EmailProviderTest {

	//Default configs to avoid boilerplate in tests
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();

	// These configs are needed to build the client, but their values don't matter for these tests since we use fakes
	private static final EmailConfig DEFAULT_EMAIL_CONFIG = new MailtrapConf("test@test.com", "api-token", true, 123123);

    @Test
    void send_storesEmail_andReturnsSuccessResult() {
        FakeEmailProvider provider = new FakeEmailProvider();
		provider.setProviderConfig(DEFAULT_EMAIL_CONFIG);
		provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
		provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

		String[] recipients = {"test@test.com"};
		String[] cc = {"cc@test.com"};
		String[] bcc = {"bcc@test.com"};

        EmailNotification email = new EmailNotification(
			recipients,
			cc,
			bcc,
			"Test Subject",
			"Test Body",
			"in-memory-email"
        );

        NotificationResult result = provider.send(email);

        assertTrue(result.isSuccess());
        assertEquals("in-memory-email", result.getProvider());
        assertNotNull(result.getMessage());

        assertEquals(1, provider.sentEmails().size());
        assertEquals(email, provider.sentEmails().get(0));
    }

    @Test
    void send_noThrowsValidationException_whenRequestInvalid() {
        FakeEmailProvider provider = new FakeEmailProvider();
		provider.setProviderConfig(DEFAULT_EMAIL_CONFIG);
		provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
		provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

		String[] recipients = {"test@test.com"};
		String[] cc = {"cc@test.com"};
		String[] bcc = {"bcc@test.com"};

        EmailNotification email = new EmailNotification(
			recipients,
			cc,
			bcc,
			"Test Subject",
			"Test Body",
			"in-memory-email"
        );

        assertDoesNotThrow( () -> provider.send(email));
    }

    @Test
    void throwsValidationErrorWhenIncorrentSettingsSend() {
        FakeEmailProvider provider = new FakeEmailProvider();
		provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
		provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

		assertThrows(ValidationException.class, () -> provider.setProviderConfig(new OneSignalConf()));
    }
}
