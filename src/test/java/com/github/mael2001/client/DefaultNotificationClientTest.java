package com.github.mael2001.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.models.FakeNotifier;
import com.github.mael2001.publisher.NotificationPublisher;

public class DefaultNotificationClientTest {

	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();

	@Test
	void send_callsRequestValidate() throws ConfigException {
        // Arrange
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL, NotificationResult.success("test-provider", NotificationChannel.EMAIL, "ok"));
		FakeNotifier fake2 = new FakeNotifier(NotificationChannel.SMS, NotificationResult.success("test-provider", NotificationChannel.EMAIL, "ok"));

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.register(NotificationChannel.EMAIL, "fake", fake)
				.register(NotificationChannel.SMS, "fake2", fake2)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		// Act & Assert
		class ValidatingRequest extends EmailNotification {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}
		}

		ValidatingRequest req = new ValidatingRequest();
		NotificationResult res = client.send(req);

		assertTrue(res.isSuccess(), "DefaultNotificationClient must call request.validate()");
	}

	@Test
	void send_routesToDefaultProvider_andReturnsProviderResult() throws ConfigException {
		// Arrange
		NotificationResult expected = NotificationResult.success("fake", NotificationChannel.EMAIL, "msg-123");
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL, expected);

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.register(NotificationChannel.EMAIL, "fake", fake)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}

		};

		NotificationResult actual = client.send(req);

		assertEquals(expected, actual);
	}

	@Test
	void send_throws_whenNoDefaultProviderConfiguredForChannel() throws ConfigException {
		// Register provider but DO NOT set defaultProvider
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL, NotificationResult.success("fake", NotificationChannel.EMAIL, "id"));

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.register(NotificationChannel.EMAIL, "fake", fake)
				.build();

		NotificationRequest req = new EmailNotification() {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}

		};
		NotificationResult failedResult = client.send(req);

		assertTrue(!failedResult.isSuccess());
		assertEquals(failedResult.getErrorType(), ErrorTypes.VALIDATION);

	}

	@Test
	void send_doesNotFail_whenEventPublisherThrows() throws ConfigException {
			FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL, NotificationResult.success("fake", NotificationChannel.EMAIL, "ok"));

		NotificationPublisher throwingPublisher = event -> {
			throw new RuntimeException("boom");
		};

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.eventPublisher(throwingPublisher)
				.register(NotificationChannel.EMAIL, "fake", fake)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}

		};

		assertDoesNotThrow(() -> client.send(req));
	}
}
