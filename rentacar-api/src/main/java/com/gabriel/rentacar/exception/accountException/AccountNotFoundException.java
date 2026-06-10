package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ResourceNotFoundException;

import java.io.Serial;

public class AccountNotFoundException extends ResourceNotFoundException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountNotFoundException(Long id) {
		super(String.format("Account not found with ID: %d ", id));
	}
}
