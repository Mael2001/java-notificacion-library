package com.github.mael2001.publisher;

import org.junit.jupiter.api.Test;

import com.github.mael2001.client.NotificationClient;
import com.github.mael2001.client.NotificationClientBuilder;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.config.sms.VonageConf;
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

	// These configs are needed to build the client, but their values don't matter for these tests since we use fakes
	private static final MailtrapConf DEFAULT_EMAIL_CONFIG = new MailtrapConf("test@test.com", "api-token", true, 123123);
	private static final OneSignalConf DEFAULT_PUSH_CONFIG = new OneSignalConf("api.onesignal.com", "api-key", "app-id", true);
	private static final VonageConf DEFAULT_SMS_CONFIG = new VonageConf("api.onesignal.com", "api-key","api-secret", "app-id", "brand-name");
	// Publisher Config
	private static final RabbitMQConfig DEFAULT_RABBIT_CONFIG = new RabbitMQConfig("fake-host", 5672, "fake-user",
			"fake-pass", "fake-virtual-host", "fake-exchange", "fake-routing-key", false, true, "fake-publisher");

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
				.registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
				.registerProviderConfiguration(DEFAULT_SMS_CONFIG)
				.registerProviderConfiguration(DEFAULT_PUSH_CONFIG)
				.registerProviderConfiguration(DEFAULT_RABBIT_CONFIG)
				.registerEventPublisher("fake-publisher", publisher)
				.register(NotificationChannel.EMAIL, "fake", fakeEmailNotifier)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel getChannel() {
				return NotificationChannel.EMAIL;
			}

			@Override
			public String correlationId() {
				return "corr-99";
			}

			@Override
			public String getProviderName() {
				return "fake";
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

		FakeEventPublisher throwingPublisher = new FakeEventPublisher() {
			@Override
			public void publish(NotificationEvent event) {
				throw new RuntimeException("Publisher failure");
			}
		};
		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
				.registerProviderConfiguration(DEFAULT_SMS_CONFIG)
				.registerProviderConfiguration(DEFAULT_PUSH_CONFIG)
				.registerProviderConfiguration(DEFAULT_RABBIT_CONFIG)
				.registerEventPublisher("fake-publisher", throwingPublisher)
				.register(NotificationChannel.EMAIL, "fake", fakeEmailNotifier)
				.defaultProvider(NotificationChannel.EMAIL, "fake")
				.build();

		NotificationRequest req = new NotificationRequest() {
			@Override
			public NotificationChannel getChannel() {
				return NotificationChannel.EMAIL;
			}

			@Override
			public String correlationId() {
				return "corr-1";
			}

			@Override
			public String getProviderName() {
				return "fake";
			}

		};

		// Act + Assert
		assertDoesNotThrow(() -> client.send(req));
	}

}
