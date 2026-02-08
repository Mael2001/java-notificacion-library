package com.github.mael2001.validation;

import com.github.mael2001.channels.EmailNotification;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.validation.validators.CommonValidator;
import com.github.mael2001.validation.validators.EmailValidator;

public class EmailNotificationValidator implements Validator<EmailNotification> {

    @Override
    public void validate(EmailNotification target) {

        // Check if Recipient is null
        if (target.getRecipients() == null) {
            throw new ValidationException("Recipient is empty");
        }

        // Itinertate through all the recipients and validate each email address
        for (String recipient : target.getRecipients()) {
            ValidationResult emailResult = EmailValidator.validEmail(recipient);
            if (!emailResult.isValid()) {
                throw new ValidationException(emailResult.toString());
            }
        }

        // Validate CC email addresses if present
        if (target.getCc() != null) {
            for (String ccEmail : target.getCc()) {
                ValidationResult ccEmailResult = EmailValidator.validEmail(ccEmail);
                if (!ccEmailResult.isValid()) {
                    throw new ValidationException(ccEmailResult.toString());
                }
            }
        }
        // Validate BCC email addresses if present
        if (target.getBcc() != null) {
            for (String bccEmail : target.getBcc()) {
                ValidationResult bccEmailResult = EmailValidator.validEmail(bccEmail);
                if (!bccEmailResult.isValid()) {
                    throw new ValidationException(bccEmailResult.toString());
                }
            }
        }

        // Validate subject and body are not blank
        ValidationResult subjectResult = CommonValidator.notBlank(target.getSubject(), "subject");
        if (!subjectResult.isValid()) {
            throw new ValidationException(subjectResult.toString());
        }
        // Validate subject and body are not blank
        ValidationResult bodyResult = CommonValidator.notBlank(target.getBody(), "body");
        if (!bodyResult.isValid()) {
            throw new ValidationException(bodyResult.toString());
        }
    }

}
