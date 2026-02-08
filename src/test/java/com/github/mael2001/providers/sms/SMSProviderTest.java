package com.github.mael2001.providers.sms;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.config.sms.VonageConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.models.FakeSMSProvider;

public class SMSProviderTest {

    // Default configs to avoid boilerplate in tests
    private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
    private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();

    // These configs are needed to build the client, but their values don't matter
    // for these tests since we use fakes
    private static final VonageConf DEFAULT_SMS_CONFIG = new VonageConf("api.onesignal.com", "api-key", "api-secret",
            "app-id", "brand-name");

    @Test
    void send_storesSMS_andReturnsSuccessResult() {
        FakeSMSProvider provider = new FakeSMSProvider();
        provider.setProviderConfig(DEFAULT_SMS_CONFIG);
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        SMSNotification sms = new SMSNotification(
                "Test Message",
                "1234564",
                "in-memory-sms");

        NotificationResult result = provider.send(sms);

        assertTrue(result.isSuccess());
        assertEquals("in-memory-sms", result.getProvider());
        assertNotNull(result.getMessage());

        assertEquals(1, provider.sentSMSNotifications().size());
        assertEquals(sms, provider.sentSMSNotifications().get(0));
    }

    @Test
    void send_noThrowsValidationException_whenRequestInvalid() {
        FakeSMSProvider provider = new FakeSMSProvider();
        provider.setProviderConfig(DEFAULT_SMS_CONFIG);
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        SMSNotification sms = new SMSNotification(
                "Test Message",
                "1234564",
                "in-memory-sms");
        assertDoesNotThrow(() -> provider.send(sms));
    }

    @Test
    void throwsValidationErrorWhenIncorrentSettingsSend() {
        FakeSMSProvider provider = new FakeSMSProvider();
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        assertThrows(ValidationException.class, () -> provider.setProviderConfig(new MailtrapConf()));
    }
}
