package com.github.mael2001.validation.Validators;

import com.github.mael2001.validation.ValidationResult;

public class CommonValidator {

	public static ValidationResult notBlank(String value, String field) {
		ValidationResult result = new ValidationResult();
		if (value == null || value.isBlank()) {
			result.add(field + " must not be blank");
		}
		return result;
	}

	public static ValidationResult maxLength(String value, int max, String field) {
		ValidationResult result = new ValidationResult();
		if (value != null && value.length() > max) {
			result.add(field + " exceeds " + max);
		}
		return result;
	}

	public static ValidationResult notNull(Object value, String field) {
		ValidationResult result = new ValidationResult();
		if (value == null) {
			result.add(field + " must not be null");
		}
		return result;
	}

}
