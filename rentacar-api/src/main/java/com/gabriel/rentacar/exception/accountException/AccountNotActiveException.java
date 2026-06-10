package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountNotActiveException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountNotActiveException(Long id) {
		super(String.format("Account with ID: %d is not active so cant rent",id),"This account isn't active so can't " +
				"rent!");
	}
}
