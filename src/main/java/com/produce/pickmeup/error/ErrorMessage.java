package com.produce.pickmeup.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
	private final String code;
	private final String message;

	public static ErrorMessage of(String code, String message) {
		return new ErrorMessage(code, message);
	}
}
