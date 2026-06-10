package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountAlreadyDeactivatedException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountAlreadyDeactivatedException(Long id) {
		super(String.format("Account with ID %d is already deactivated", id), "Account already deactivated!");
	}
}
