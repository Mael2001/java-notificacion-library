package com.github.mael2001.examples;

import com.github.mael2001.client.*;
import com.github.mael2001.config.*;
import com.github.mael2001.dto.*;
import com.github.mael2001.config.email.EmailConfig;
import com.github.mael2001.config.push.PushConfig;
import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.config.sms.SMSConfig;
import com.github.mael2001.providers.email.*;
import com.github.mael2001.providers.sms.*;
import com.github.mael2001.publisher.RabbitMQEventPublisher;
import com.github.mael2001.providers.push.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationExamples {

	private static final GlobalConfig DEFAULT_GLOBAL_CONFIG = new GlobalConfig();
	private static final RetryConfig DEFAULT_RETRY_CONFIG = new RetryConfig();

	// Publisher Config
	private static final RabbitMQConfig DEFAULT_RABBIT_CONFIG = new RabbitMQConfig("fake-host", 5672, "fake-user",
			"fake-pass", "fake-virtual-host", "fake-exchange", "fake-routing-key", false, true, "fake-publisher");

	public static void main(String[] args) {
		try {
			log.info("Starting Configs");
			// Email Configs
			EmailConfig mailTrapConfig = new EmailConfig("smtp.mailtrap.io", 2525, "your_mailtrap_username",
					"your_mailtrap_password", true, true, "your_mailtrap_email_from", "mailtrap");
			EmailConfig sendGridConfig = new EmailConfig("smtp.sendgrid.net", 587, "your_sendgrid_username",
					"your_sendgrid_password", true, true, "your_sendgrid_email_from", "sendgrip");
			// SMS Configs
			SMSConfig twilioConfig = new SMSConfig("your_twilio_api_key", "your_twilio_api_url", "twilio");
			SMSConfig acousticConfig = new SMSConfig("your_acoustic_api_key", "your_acoustic_api_url", "acoustic");
			// Push Configs
			PushConfig oneSignalConfig = new PushConfig("your_one_signal_api_key", "your_one_signal_api_url",
					"one-signal");
			PushConfig kumulosConfig = new PushConfig("your_kumulos_api_key", "your_kumulos_api_url", "kumulos");

			// Publisher Configs
			RabbitMQConfig rabbitConfig = new RabbitMQConfig("your_rabbitmq_host", 5672, "your_rabbitmq_user",
					"your_rabbitmq_pass", "your_rabbitmq_virtual_host",
					"your_rabbitmq_exchange", "your_rabbitmq_routing_key", false, true, "rabbit-mq");

			log.info("Configurations Created");

			log.info("Creating Notification Client");
			// Client NotificationBuilder
			NotificationClient client = NotificationClientBuilder.create()
					.globalConfig(DEFAULT_GLOBAL_CONFIG)
					.retryConfig(DEFAULT_RETRY_CONFIG)
					.registerProviderConfiguration(mailTrapConfig)
					.registerProviderConfiguration(sendGridConfig)
					.registerProviderConfiguration(twilioConfig)
					.registerProviderConfiguration(acousticConfig)
					.registerProviderConfiguration(oneSignalConfig)
					.registerProviderConfiguration(kumulosConfig)
					.registerProviderConfiguration(rabbitConfig)
					// You can register your providers here using .register(channel, providerName,
					// providerInstance)
					// For example:
					// .register(NotificationChannel.EMAIL, "fake-email-provider", new
					// FakeEmailProvider())
					// .register(NotificationChannel.SMS, "fake-sms-provider", new
					// FakeSMSProvider())
					// .register(NotificationChannel.PUSH, "fake-push-provider", new
					// FakePushProvider())
					.register(NotificationChannel.EMAIL, "mailtrap", new MailtrapEmailProvider())
					.register(NotificationChannel.EMAIL, "sendgrip", new SendGridEmailProvider())
					.register(NotificationChannel.SMS, "twilio", new TwilioProvider())
					.register(NotificationChannel.SMS, "acoustic", new AcousticProvider())
					.register(NotificationChannel.PUSH, "one-signal", new OneSignalProvider())
					.register(NotificationChannel.PUSH, "kumulos", new KumulosProvider())
					.registerEventPublisher("rabbit-mq", new RabbitMQEventPublisher())
					.defaultProvider(NotificationChannel.EMAIL, "mailtrap")
					.defaultProvider(NotificationChannel.SMS, "twilio")
					.defaultProvider(NotificationChannel.PUSH, "one-signal")
					.build();

			log.info("Notification Client Created Successfully");

		} catch (Exception e) {
			log.error("Error initializing notification client: {}", e.getMessage());
			e.printStackTrace();
			return;
		}

	}
}
