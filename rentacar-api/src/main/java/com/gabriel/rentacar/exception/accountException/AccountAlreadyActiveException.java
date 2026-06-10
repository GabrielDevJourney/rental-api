package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountAlreadyActiveException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountAlreadyActiveException(Long id) {
		super(String.format("Account with ID %d is already active", id),"Account already active!");
	}
}
