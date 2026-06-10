package com.gabriel.rentacar.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	private final String clientMessage;
	public ValidationException(String message, String clientMessage) {
		super(message);
		this.clientMessage = clientMessage;
	}

}