package com.github.mael2001.validation;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.validation.validators.CommonValidator;

public class SMSNotificationValidator implements Validator<SMSNotification> {

	@Override
	public void validate(SMSNotification target) {

		ValidationResult messageResult = CommonValidator.notBlank(target.getMessage(), "message");
		if (!messageResult.isValid()) {
			throw new ValidationException(messageResult.toString());
		}

		ValidationResult phoneResult = CommonValidator.notBlank(target.getPhoneNumber(), "phoneNumber");
		if (!phoneResult.isValid()) {
			throw new ValidationException(phoneResult.toString());
		}
	}

}
