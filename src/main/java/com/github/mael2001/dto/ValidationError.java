package com.github.mael2001.dto;

import lombok.Getter;
import lombok.Setter;

public class ValidationError {
	@Getter @Setter private String field;
	@Getter @Setter private String message;
}
