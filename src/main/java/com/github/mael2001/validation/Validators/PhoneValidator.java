package com.github.mael2001.validation.validators;

import java.util.regex.Pattern;

import com.github.mael2001.validation.ValidationResult;

public class PhoneValidator {
	public class EmailValidator {

	private static final String VALID_PHONE_REGEX = "^(?!\\\\b(0)\\\\1+\\\\b)(\\\\+?\\\\d{1,3}[. -]?)?\\\\(?\\\\d{3}\\\\)?([. -]?)\\\\d{3}\\\\3\\\\d{4}$";

    public static ValidationResult validPhone(String phone) {
        ValidationResult result = new ValidationResult();
		if (phone == null || phone.isBlank()) {
			result.add("Phone must not be blank");
		}

		Pattern pattern = Pattern.compile(VALID_PHONE_REGEX);
		if (!pattern.matcher(phone).matches()) {
			result.add("Invalid phone number");
		}

        return result;
    }
}

}
