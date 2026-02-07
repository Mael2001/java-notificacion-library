package com.github.mael2001.validation.Validators;

import java.util.regex.Pattern;

import com.github.mael2001.validation.ValidationResult;

public class EmailValidator {

	private static final String VALID_EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static ValidationResult validEmail(String email) {
        ValidationResult result = new ValidationResult();
		if (email == null || email.isBlank()) {
			result.add("Email must not be blank");
		}

		Pattern pattern = Pattern.compile(VALID_EMAIL_REGEX);
		if (email != null && !pattern.matcher(email).matches()) {
			result.add("Invalid email address");
		}

        return result;
    }
}
