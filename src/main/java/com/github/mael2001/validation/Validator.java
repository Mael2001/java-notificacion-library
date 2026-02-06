package com.github.mael2001.validation;

import com.github.mael2001.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T target) throws ValidationException;
}