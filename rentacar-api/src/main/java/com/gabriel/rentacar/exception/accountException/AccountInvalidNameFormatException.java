package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountInvalidNameFormatException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidNameFormatException(String fieldName,String reason) {
		super(String.format("Invalid %s format: %s", fieldName, reason),
				String.format("Please input a valid %s", fieldName.toLowerCase()));
	}
}
