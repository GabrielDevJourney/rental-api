package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountInvalidDataException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidDataException(String field, String message) {
		super(String.format("Invalid account data: %s - %s", field, message),
				String.format("Please provide valid %s information", field));
	}
}