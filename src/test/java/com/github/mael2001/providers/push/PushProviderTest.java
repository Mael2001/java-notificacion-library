package com.github.mael2001.providers.push;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.config.sms.VonageConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.models.FakePushProvider;

public class PushProviderTest {

    // Default configs to avoid boilerplate in tests
    private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
    private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();
    // These configs are needed to build the client, but their values don't matter
    // for these tests since we use fakes
    private static final OneSignalConf DEFAULT_PUSH_CONFIG = new OneSignalConf("api.onesignal.com", "api-key", "app-id",
            true);

    @Test
    void send_storesPush_andReturnsSuccessResult() {
        FakePushProvider provider = new FakePushProvider();
        provider.setProviderConfig(DEFAULT_PUSH_CONFIG);
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        PushNotification push = new PushNotification(
                "Test Title",
                "Test Message",
                "1234564",
                "fake",
                false);

        NotificationResult result = provider.send(push);

        assertTrue(result.isSuccess());
        assertEquals("in-memory-push", result.getProvider());
        assertNotNull(result.getMessage());

        assertEquals(1, provider.sentPushNotifications().size());
        assertEquals(push, provider.sentPushNotifications().get(0));
    }

    @Test
    void send_noThrowsValidationException_whenRequestInvalid() {
        FakePushProvider provider = new FakePushProvider();
        provider.setProviderConfig(DEFAULT_PUSH_CONFIG);
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        PushNotification push = new PushNotification(
                "Test Title",
                "Test Message",
                "1234564",
                "fake",
                false);
        assertDoesNotThrow(() -> provider.send(push));
    }

    @Test
    void throwsValidationErrorWhenIncorrentSettingsSend() {
        FakePushProvider provider = new FakePushProvider();
        provider.setGlobalConfig(DEFAULT_GLOBAL_CONFIG);
        provider.setRetryConfig(DEFAULT_RETRY_CONFIG);

        assertThrows(ValidationException.class, () -> provider.setProviderConfig(new VonageConf()));
    }
}
