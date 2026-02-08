package com.github.mael2001.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.config.sms.VonageConf;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.models.FakeEventPublisher;
import com.github.mael2001.models.FakeNotifier;

public class DefaultNotificationClientTest {

	//Default configs to avoid boilerplate in tests
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();
	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();

	// These configs are needed to build the client, but their values don't matter for these tests since we use fakes
	private static final EmailConfig DEFAULT_EMAIL_CONFIG = new MailtrapConf("test@test.com", "api-token", true, 123123);
	private static final OneSignalConf DEFAULT_PUSH_CONFIG = new OneSignalConf("api.onesignal.com", "api-key", "app-id", true);
	private static final VonageConf DEFAULT_SMS_CONFIG = new VonageConf("api.onesignal.com", "api-key","api-secret", "app-id", "brand-name");

	// Publisher Config
	private static final RabbitMQConfig DEFAULT_RABBIT_CONFIG = new RabbitMQConfig("fake-host", 5672, "fake-user",
			"fake-pass", "fake-virtual-host", "fake-exchange", "fake-routing-key", false, true, "fake-publisher");


	@Test
	void send_callsRequestValidate() throws ConfigException {
		// Arrange
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL,
				NotificationResult.success("fake-email-provider", NotificationChannel.EMAIL, "ok"));
		FakeNotifier fake2 = new FakeNotifier(NotificationChannel.SMS,
				NotificationResult.success("fake-sms-provider", NotificationChannel.SMS, "ok"));
		FakeNotifier fake3 = new FakeNotifier(NotificationChannel.PUSH,
				NotificationResult.success("fake-push-provider", NotificationChannel.PUSH, "ok"));

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
				.registerProviderConfiguration(DEFAULT_SMS_CONFIG)
				.registerProviderConfiguration(DEFAULT_PUSH_CONFIG)
				.register(NotificationChannel.EMAIL, "fake-email-provider", fake)
				.register(NotificationChannel.SMS, "fake-sms-provider", fake2)
				.register(NotificationChannel.PUSH, "fake-push-provider", fake3)
				.defaultProvider(NotificationChannel.EMAIL, "fake-email-provider")
				.build();

		// Act & Assert
		class ValidatingRequest extends EmailNotification {
			@Override
			public NotificationChannel getChannel() {
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
		NotificationResult expected = NotificationResult.success("fake-email-provider", NotificationChannel.EMAIL,
				"msg-123");
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL, expected);

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
				.registerProviderConfiguration(DEFAULT_SMS_CONFIG)
				.registerProviderConfiguration(DEFAULT_PUSH_CONFIG)
				.register(NotificationChannel.EMAIL, "fake-email-provider", fake)
				.defaultProvider(NotificationChannel.EMAIL, "fake-email-provider")
				.build();

		NotificationRequest req = new EmailNotification() {
			@Override
			public NotificationChannel getChannel() {
				return NotificationChannel.EMAIL;
			}

		};

		NotificationResult actual = client.send(req);

		assertEquals(expected, actual);
	}

	@Test
	void send_throws_whenNoDefaultProviderConfiguredForChannel() throws ConfigException {
		// Register provider but DO NOT set defaultProvider
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL,
				NotificationResult.success("fake", NotificationChannel.EMAIL, "id"));

		NotificationClient client = NotificationClientBuilder.create()
				.globalConfig(DEFAULT_GLOBAL_CONFIG)
				.retryConfig(DEFAULT_RETRY_CONFIG)
				.registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
				.registerProviderConfiguration(DEFAULT_SMS_CONFIG)
				.registerProviderConfiguration(DEFAULT_PUSH_CONFIG)
				.register(NotificationChannel.EMAIL, "fake-email-provider", fake)
				.build();

		NotificationRequest req = new EmailNotification() {
			@Override
			public NotificationChannel getChannel() {
				return NotificationChannel.EMAIL;
			}

		};
		NotificationResult failedResult = client.send(req);

		assertTrue(!failedResult.isSuccess());
		assertEquals(failedResult.getErrorType(), ErrorTypes.VALIDATION);

	}

	@Test
	void send_doesNotFail_whenEventPublisherThrows() throws ConfigException {
		FakeNotifier fake = new FakeNotifier(NotificationChannel.EMAIL,
				NotificationResult.success("fake", NotificationChannel.EMAIL, "ok"));

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
				.register(NotificationChannel.EMAIL, "fake-email-provider", fake)
				.defaultProvider(NotificationChannel.EMAIL, "fake-email-provider")
				.build();

		NotificationRequest req = new EmailNotification() {
			@Override
			public NotificationChannel getChannel() {
				return NotificationChannel.EMAIL;
			}

		};

		assertDoesNotThrow(() -> client.send(req));
	}
}
