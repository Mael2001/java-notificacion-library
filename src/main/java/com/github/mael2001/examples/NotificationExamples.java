package com.github.mael2001.examples;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.client.*;
import com.github.mael2001.config.*;
import com.github.mael2001.dto.*;
import com.github.mael2001.config.email.MailtrapConf;
import com.github.mael2001.config.email.ResendConf;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.config.sms.VonageConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.providers.email.*;
import com.github.mael2001.providers.sms.*;
import com.github.mael2001.publisher.RabbitMQEventPublisher;
import com.github.mael2001.providers.push.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationExamples {

	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();

	// These configs are needed to build the client, but their values don't matter
	// for these tests since we use fakes
	private static final MailtrapConf MAILTRAP_CONF = new MailtrapConf("test@test.com",
			"api-key", true, 4368086);
	private static final ResendConf RESEND_CONF = new ResendConf("test@test.com",
			"api-key");
	private static final OneSignalConf ONESIGNAL_CONF = new OneSignalConf("api.onesignal.com", "api-key", "app-id",
			true);
	private static final VonageConf VONAGE_CONF = new VonageConf("api.onesignal.com", "api-key", "api-secret", "app-id",
			"brand-name");
	// Publisher Config
	private static final RabbitMQConfig DEFAULT_RABBIT_CONFIG = new RabbitMQConfig("your_rabbitmq_host", 5672,
			"your_rabbitmq_user",
			"your_rabbitmq_pass", "your_rabbitmq_virtual_host",
			"your_rabbitmq_exchange", "your_rabbitmq_routing_key", false, true, "rabbit-mq");

	public static void main(String[] args) {
		try {
			log.info("Creating Notification Client");
			// Client NotificationBuilder
			NotificationClient client = NotificationClientBuilder.create()
					.globalConfig(DEFAULT_GLOBAL_CONFIG)
					.retryConfig(DEFAULT_RETRY_CONFIG)
					.registerProviderConfiguration(MAILTRAP_CONF)
					.registerProviderConfiguration(RESEND_CONF)
					.registerProviderConfiguration(ONESIGNAL_CONF)
					.registerProviderConfiguration(VONAGE_CONF)
					.registerProviderConfiguration(DEFAULT_RABBIT_CONFIG)
					// You can register your providers here using .register(channel, providerName,
					// providerInstance)
					// For example:
					// .register(NotificationChannel.EMAIL, "fake-email-provider", new
					// FakeEmailProvider())
					// .register(NotificationChannel.SMS, "fake-sms-provider", new
					// FakeSMSProvider())
					// .register(NotificationChannel.PUSH, "fake-push-provider", new
					// FakePushProvider())
					.register(NotificationChannel.EMAIL, "Mailtrap", new MailtrapEmailProvider())
					.register(NotificationChannel.EMAIL, "Resend", new ResendEmailProvider())
					.register(NotificationChannel.PUSH, "Onesignal", new OneSignalProvider())
					.register(NotificationChannel.SMS, "Vonage", new VonageProvider())
					.registerEventPublisher("rabbit-mq", new RabbitMQEventPublisher())
					.defaultProvider(NotificationChannel.EMAIL, "Mailtrap")
					.defaultProvider(NotificationChannel.SMS, "Resend")
					.defaultProvider(NotificationChannel.PUSH, "Onesignal")
					.build();

			log.info("Notification Client Created Successfully");

			log.info("Send Mailtrap Email Notification");

			String[] recipients = { "test@test.com" };

			NotificationResult mailtrapRes = client.send(new EmailNotification(recipients, "Title", "Body", "Mailtrap", false));

			log.info("result sucess:{}, channel:{}, errMsg:{}, errorType:{}, msg:{}, provider;{}", mailtrapRes.isSuccess(),
					mailtrapRes.getChannel(), mailtrapRes.getErrorMessage(), mailtrapRes.getErrorType(), mailtrapRes.getMessage(), mailtrapRes.getProvider());


			log.info("Send Resend Email Notification");

			NotificationResult resendRes = client.send(new EmailNotification(recipients, "Title", "Body", "Mailtrap", false));

			log.info("result sucess:{}, channel:{}, errMsg:{}, errorType:{}, msg:{}, provider;{}", resendRes.isSuccess(),
					resendRes.getChannel(), resendRes.getErrorMessage(), resendRes.getErrorType(), resendRes.getMessage(), resendRes.getProvider());


			log.info("Send Onesignal Push Notification");


			NotificationResult oneSignalRes = client.send(new PushNotification("test title", "test msg", "token", "Onesignal", false));

			log.info("result sucess:{}, channel:{}, errMsg:{}, errorType:{}, msg:{}, provider;{}", oneSignalRes.isSuccess(),
					oneSignalRes.getChannel(), oneSignalRes.getErrorMessage(), oneSignalRes.getErrorType(), oneSignalRes.getMessage(), oneSignalRes.getProvider());


			log.info("Send Vonage SMS Notification");

			NotificationResult vonageRes = client.send(new SMSNotification("Test Msg", "+455615245", "Vonage", false));

			log.info("result sucess:{}, channel:{}, errMsg:{}, errorType:{}, msg:{}, provider;{}", vonageRes.isSuccess(),
					vonageRes.getChannel(), vonageRes.getErrorMessage(), vonageRes.getErrorType(), vonageRes.getMessage(), vonageRes.getProvider());

		} catch (Exception e) {
			log.error("Error initializing notification client: {}", e.getMessage());
			e.printStackTrace();
			return;
		}

	}
}
