package com.github.mael2001.client;

import com.github.mael2001.channels.email.EmailNotification;
import com.github.mael2001.channels.push.PushNotification;
import com.github.mael2001.channels.sms.SMSNotification;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.dto.Notifier;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.publisher.NotificationPublisher;
import com.github.mael2001.validation.EmailNotificationValidator;
import com.github.mael2001.validation.PushNotificationValidator;
import com.github.mael2001.validation.SMSNotificationValidator;
import com.github.mael2001.validation.Validator;

import java.security.ProviderException;
import java.time.Instant;
import java.util.Map;

class DefaultNotificationClient implements NotificationClient {

	private final Map<NotificationChannel, Map<String, Notifier<?>>> providers;
	private final Map<NotificationChannel, String> defaults;
	private final NotificationPublisher eventPublisher;
	private final Map<Class<?>, Validator<?>> validators = Map.of(
			EmailNotification.class, new EmailNotificationValidator(),
			PushNotification.class, new PushNotificationValidator(),
			SMSNotification.class, new SMSNotificationValidator());

	DefaultNotificationClient(
			Map<NotificationChannel, Map<String, Notifier<?>>> providers,
			Map<NotificationChannel, String> defaults,
			NotificationPublisher eventPublisher) {
		this.providers = providers;
		this.defaults = defaults;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public NotificationResult send(NotificationRequest request) {

		try {
			// Check If request is null
			if (request == null) {
				throw new ValidationException("Request is empty");
			}

			// Validate request based on its type
			Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
					.get(request.getClass());
			// Check for error in validation
			if (validator != null) {
				validator.validate(request);
			}

			// Get the channel of the request
			NotificationChannel channel = request.channel();

			// Get the default provider for the channel
			String defaultProvider = defaults.get(channel);

			// Check if default provider is set
			if (defaultProvider == null) {
				throw new ValidationException("No default provider set for channel: " + channel);
			}

			// Get the notifier for the channel and provider
			Notifier<?> notifier = providers
					.getOrDefault(channel, Map.of())
					.get(defaultProvider);

			// Check if notifier is found
			if (notifier == null) {
				throw new ValidationException(
						"No provider found for channel: " + channel + " and provider: " + defaultProvider);
			}

			// Cast the notifier to the correct type
			Notifier<NotificationRequest> typed = (Notifier<NotificationRequest>) notifier;

			// Send the request and return the result
			NotificationResult result = typed.send(request);

			//Check if event publisher is set, if yes publish the event
			if (eventPublisher != null) {
				// Gemerate random id for the event
				String eventId = java.util.UUID.randomUUID().toString();
				// Create the event
				NotificationEvent event = new NotificationEvent(
						eventId,
						result.getProvider(),
						result.getChannel(),
						Instant.now(),
						result);

				// Publish the event
				eventPublisher.publish(event);
			}
			// Return the result
			return result;
		} catch (ValidationException ex) {
			return NotificationResult.failure(
					"Validation",
					ErrorTypes.VALIDATION,
					ex.getMessage());
		} catch (ProviderException ex) {
			return NotificationResult.failure(
					"Provider",
					ErrorTypes.PROVIDER,
					ex.getMessage());
		} catch (Exception ex) {
			return NotificationResult.failure(
					"Unknown",
					ErrorTypes.UNKNOWN,
					ex.getMessage());
		}
	}
}
