package com.github.mael2001.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void add(String error) {
        errors.add(error);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> errors() {
        return List.copyOf(errors);
    }

	public String toString() {
		return String.join(", ", errors);
	}
}