package com.github.mael2001.channels;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.validation.EmailNotificationValidator;
import com.github.mael2001.validation.PushNotificationValidator;
import com.github.mael2001.validation.SMSNotificationValidator;
import com.github.mael2001.validation.Validator;

public class PushNotificationTest {

	private final Map<Class<?>, Validator<?>> validators = Map.of(
			EmailNotification.class, new EmailNotificationValidator(),
			PushNotification.class, new PushNotificationValidator(),
			SMSNotification.class, new SMSNotificationValidator());

	@Test
	void channel_isPush() {
		var n = new PushNotification();
		assertEquals(NotificationChannel.PUSH, n.channel());
	}

	@Test
	void validate_throws_whenMessageInvalid() {
		var n = new PushNotification();
		n.setTitle("test-title");
		n.setRecipientDeviceToken("token");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertThrows(ValidationException.class, () -> validator.validate(n));
	}

	@Test
	void validate_throws_whenTitleInvalid() {
		var n = new PushNotification();
		n.setMessage("test-title");
		n.setRecipientDeviceToken("token");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertThrows(ValidationException.class, () -> validator.validate(n));
	}

	@Test
	void validate_throws_whenDeviceTokenInvalid() {
		var n = new PushNotification();
		n.setTitle("test-title");
		n.setMessage("test-message");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertThrows(ValidationException.class, () -> validator.validate(n));
	}

	@Test
	void validate_ok_whenValid() {
		var n = new PushNotification();
		n.setTitle("test-title");
		n.setMessage("test-message");
		n.setRecipientDeviceToken("token");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		assertDoesNotThrow(() -> validator.validate(n));
	}
}
