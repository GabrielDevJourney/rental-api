package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountEmailAlreadyExistsException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountEmailAlreadyExistsException(String email) {
		super(String.format("Email already exists: %s ", email),"Invalid email, already registered");
	}
}