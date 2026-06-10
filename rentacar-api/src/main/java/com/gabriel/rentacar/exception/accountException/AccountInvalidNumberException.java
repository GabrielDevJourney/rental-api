package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

@SuppressWarnings("SpellCheckingInspection")
public class AccountInvalidNumberException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public AccountInvalidNumberException(String phoneNumber) {
		super(String.format(" %s is an invalid format. Ensure 91,92,93,96!", phoneNumber),"Phone number with wrong " +
				"format " +
				"ensure" +
				" " +
				"91/92/93/96xxxxxxx");
	}
}
