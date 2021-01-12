package com.produce.pickmeup.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorMessage {
	private final HttpStatus status;
	private final String message;
}
