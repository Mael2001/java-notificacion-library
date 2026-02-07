package com.github.mael2001.publisher;

import org.junit.jupiter.api.Test;

import com.github.mael2001.client.NotificationClient;
import com.github.mael2001.client.NotificationClientBuilder;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.models.FakeEventPublisher;
import com.github.mael2001.models.FakeNotifier;

import static org.junit.jupiter.api.Assertions.*;

class EventPublishingTest {

	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();

	@Test
	void send_publishesEvent_withExpectedFields() throws ConfigException {
		// Arrange
		NotificationResult expectedResult = NotificationResult.success("test-provider", NotificationChannel.EMAIL,
				"ok");
		FakeNotifier fakeEmailNotifier = new FakeNotifier(NotificationChannel.EMAIL, expectedResult);

		FakeEventPublisher publisher = new FakeEventPublisher();

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.eventPublisher(publisher)
				.register(NotificationChannel.EMAIL, "fake", fakeEmailNotifier)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}

			@Override
			public String correlationId() {
				return "corr-99";
			}

		};

		// Act
		NotificationResult actual = client.send(req);

		// Assert: send result
		assertTrue(actual.isSuccess());

		// Assert: event published once
		assertEquals(1, publisher.getEvents().size());

		NotificationEvent event = publisher.getEvents().get(0);

		assertNotNull(event.getOccurredAt());
		assertNotNull(event.getId());
		assertNotNull(event.getResult());
		assertEquals(NotificationChannel.EMAIL, event.getChannel());

		// Provider name (the selected default provider)
		assertEquals("test-provider", event.getProvider());

		// Result embedded
		assertEquals(true, event.getResult().isSuccess());
	}

	@Test
	void send_doesNotFail_whenEventPublisherThrows() throws ConfigException {
		// Arrange
		FakeNotifier fakeEmailNotifier = new FakeNotifier(NotificationChannel.EMAIL,
				NotificationResult.success("test-provider", NotificationChannel.EMAIL, "ok"));

		NotificationPublisher throwingPublisher = event -> {
			throw new RuntimeException("boom");
		};

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.eventPublisher(throwingPublisher)
				.register(NotificationChannel.EMAIL, "fake", fakeEmailNotifier)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel channel() {
				return NotificationChannel.EMAIL;
			}

			@Override
			public String correlationId() {
				return "corr-1";
			}

		};

		// Act + Assert
		assertDoesNotThrow(() -> client.send(req));
	}

}
