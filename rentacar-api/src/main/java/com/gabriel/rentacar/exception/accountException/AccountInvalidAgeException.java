package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountInvalidAgeException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidAgeException(Long id ) {
		super(String.format("Invalid age for ID: %d",id), "Please enter a valid age from 18 - 99 years!");
	}
	public AccountInvalidAgeException(String email ) {
		super(String.format("Invalid age for account creation with email: %s ",email), "Please enter a valid age from" +
				" " +
				"18 - " +
				"99 " +
				"years!");
	}
}
