package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountInvalidAuthException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidAuthException() {
		super("InvalidAuth failed: Invalid credentials",
				"Invalid email or password");
	}
}