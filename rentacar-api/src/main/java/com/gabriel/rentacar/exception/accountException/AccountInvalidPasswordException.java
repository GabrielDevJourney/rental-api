package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountInvalidPasswordException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidPasswordException(String reason) {
		super(String.format("Invalid password: %s at auth service", reason),
				String.format("Password is invalid: %s", reason));
	}
}
