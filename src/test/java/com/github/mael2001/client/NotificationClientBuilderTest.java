package com.github.mael2001.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.api.NotificationClient;
import com.github.mael2001.api.NotificationClientBuilder;
import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.models.FakeGlobalAwareNotifier;
import com.github.mael2001.models.FakeNotifier;
import com.github.mael2001.models.FakeRetryAwareNotifier;

public class NotificationClientBuilderTest {

	//Default configs to avoid boilerplate in tests
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();

    @Test
    void build_throws_whenGlobalConfigMissing() {
        assertThrows(ConfigException.class, () ->
            NotificationClientBuilder.create()
				.retryConfig(DEFAULT_RETRY_CONFIG)
                .build()
        );
    }

    @Test
    void build_throws_whenRetryConfigMissing() {
        assertThrows(ConfigException.class, () ->
            NotificationClientBuilder.create()
                .globalConfig(DEFAULT_GLOBAL_CONFIG)
                .build()
        );
    }

    @Test
    void build_returnsAClient_whenGlobalAndRetryConfigProvided() throws ConfigException {
        NotificationClient client =
            NotificationClientBuilder.create()
                .globalConfig(DEFAULT_GLOBAL_CONFIG)
                .retryConfig(DEFAULT_RETRY_CONFIG)
                .build();

        assertNotNull(client);
    }

    @Test
    void register_storesProvider_andDefaultProviderIsUsedAtRuntime() throws ConfigException {
        FakeNotifier fake =
            new FakeNotifier(NotificationChannel.EMAIL, NotificationResult.success("fake",NotificationChannel.SMS, "id"));

        NotificationClient client =
            NotificationClientBuilder.create()
                .globalConfig(DEFAULT_GLOBAL_CONFIG)
                .retryConfig(DEFAULT_RETRY_CONFIG)
                .register(NotificationChannel.SMS, "fake", fake)
                .defaultProvider(NotificationChannel.SMS, "fake")
                .build();

        // Verify wiring indirectly by sending a request
        NotificationRequest req = new SMSNotification() {
            @Override public NotificationChannel getChannel() { return NotificationChannel.SMS; }
        };

        NotificationResult res = client.send(req);
        assertTrue(res.isSuccess());
    }

    @Test
    void build_injectsGlobalConfig_intoGlobalConfigAwareProviders() throws ConfigException {
        FakeGlobalAwareNotifier aware = new FakeGlobalAwareNotifier(NotificationChannel.EMAIL);

        NotificationClientBuilder.create()
            .globalConfig(DEFAULT_GLOBAL_CONFIG)
			.retryConfig(DEFAULT_RETRY_CONFIG)
            .register(NotificationChannel.EMAIL, "aware", aware)
            .defaultProvider(NotificationChannel.EMAIL, "aware")
            .build();

        assertNotNull(aware.received, "GlobalConfig must be injected into GlobalConfigAware providers");
    }

    @Test
    void build_injectsRetryConfig_intoRetryConfigAwareProviders() throws ConfigException {
        FakeRetryAwareNotifier aware = new FakeRetryAwareNotifier(NotificationChannel.EMAIL);

        NotificationClientBuilder.create()
            .globalConfig(DEFAULT_GLOBAL_CONFIG)
			.retryConfig(DEFAULT_RETRY_CONFIG)
            .register(NotificationChannel.EMAIL, "aware", aware)
            .defaultProvider(NotificationChannel.EMAIL, "aware")
            .build();

        assertNotNull(aware.received, "RetryConfig must be injected into RetryConfigAware providers");
    }

    @Test
    void build_withNullEventPublisher() throws ConfigException {
        // If builder allows null, it should not blow up.
        NotificationClient client =
            NotificationClientBuilder.create()
                .globalConfig(DEFAULT_GLOBAL_CONFIG)
                .retryConfig(DEFAULT_RETRY_CONFIG)
                .build();

        assertNotNull(client);
    }
}
