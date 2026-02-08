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

public class SMSNotificationTest {

	private final Map<Class<?>, Validator<?>> validators = Map.of(
			EmailNotification.class, new EmailNotificationValidator(),
			PushNotification.class, new PushNotificationValidator(),
			SMSNotification.class, new SMSNotificationValidator());

    @Test
    void channel_isSms() {
        var n = new SMSNotification();
        assertEquals(NotificationChannel.SMS, n.getChannel());
    }

    @Test
    void validate_throws_whenPhoneInvalid() {
        var n = new SMSNotification();
		n.setMessage("msg");
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertThrows(ValidationException.class, () -> validator.validate(n));
    }

    @Test
    void validate_throws_whenMessageBlank() {
        var n = new SMSNotification();
		n.setPhoneNumber("1234567890");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertThrows(ValidationException.class, () -> validator.validate(n));
    }

    @Test
    void validate_ok_whenValid() {
        var n = new SMSNotification();
		n.setMessage("msg");
		n.setPhoneNumber("1234567890");
		// Validate request based on its type
		Validator<NotificationRequest> validator = (Validator<NotificationRequest>) validators
				.get(n.getClass());
		// Check for error in validation
		assertDoesNotThrow(() -> validator.validate(n));
    }
}
