package com.github.mael2001.validation;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.validation.Validators.EmailValidator;
import com.github.mael2001.validation.Validators.CommonValidator;

public class EmailNotificationValidator implements Validator<EmailNotification> {

	@Override
	public void validate(EmailNotification target) {
        ValidationResult emailResult = EmailValidator.validEmail(target.getRecipient());
        if (!emailResult.isValid()) {
            throw new ValidationException(emailResult.toString());
        }
        ValidationResult subjectResult =CommonValidator.notBlank(target.getSubject(), "subject");
        if (!subjectResult.isValid()) {
            throw new ValidationException(subjectResult.toString());
        }
        ValidationResult bodyResult =CommonValidator.notBlank(target.getBody(), "body");
        if (!bodyResult.isValid()) {
            throw new ValidationException(bodyResult.toString());
        }
	}

}
