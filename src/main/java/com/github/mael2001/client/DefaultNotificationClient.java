package com.github.mael2001.client;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.channels.SMSNotification;
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.ProviderException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@AllArgsConstructor
class DefaultNotificationClient implements NotificationClient {

	private final Map<String, Notifier<?>> providers;
	private final Map<NotificationChannel, String> defaults;
	private final Map<String, NotificationPublisher> eventPublishers;

	private final Map<Class<?>, Validator<?>> validators = Map.of(
			EmailNotification.class, new EmailNotificationValidator(),
			PushNotification.class, new PushNotificationValidator(),
			SMSNotification.class, new SMSNotificationValidator());

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
			NotificationChannel channel = request.getChannel();

			// Get the provider of the request
			String provider = request.getProviderName();

			// Get the default provider for the channel
			String defaultProviderName = defaults.get(channel);

			// Get the notifier for the channel and provider
			Notifier<?> defaultProvider = providers
					.get(defaultProviderName);

			// Check if default provider is set
			if (defaultProvider == null) {
				throw new ValidationException("No default provider configured for channel: " + channel);
			}
			// Get the notifier for the channel and provider
			Notifier<?> notifier = providers
					.getOrDefault(provider, defaultProvider);

			// Check if notifier is found
			if (notifier == null) {
				throw new ValidationException(
						"No provider found for channel: " + channel + " and provider: " + defaultProvider);
			}

			// Cast the notifier to the correct type
			Notifier<NotificationRequest> typed = (Notifier<NotificationRequest>) notifier;

			// Send the request and return the result
			NotificationResult result = request.isAsync() ? typed.sendAsync(request) : typed.send(request);

			// Check if event publisher is set, if yes publish the event
			if (eventPublishers != null) {
				// Itinerate over all event publishers and publish the event to each of them
				for (NotificationPublisher eventPublisher : eventPublishers.values()) {
					// Gemerate random id for the event
					String eventId = java.util.UUID.randomUUID().toString();
					// Create the event
					NotificationEvent event = new NotificationEvent(
							eventId,
							result.getChannel(),
							result.getProvider(),
							result,
							Instant.now());

					// Publish the event
					eventPublisher.publish(event);
				}
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
