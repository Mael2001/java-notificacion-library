package com.github.mael2001.validation;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.validation.validators.CommonValidator;

public class PushNotificationValidator implements Validator<PushNotification> {

	@Override
	public void validate(PushNotification target) {

		ValidationResult titleResult = CommonValidator.notBlank(target.getTitle(), "title");
		if (!titleResult.isValid()) {
			throw new ValidationException(titleResult.toString());
		}
		ValidationResult messageResult = CommonValidator.notBlank(target.getMessage(), "message");
		if (!messageResult.isValid()) {
			throw new ValidationException(messageResult.toString());
		}
		ValidationResult deviceTokenResult = CommonValidator.notBlank(target.getRecipientDeviceToken(), "deviceToken");
		if (!deviceTokenResult.isValid()) {
			throw new ValidationException(deviceTokenResult.toString());
		}
	}

}
