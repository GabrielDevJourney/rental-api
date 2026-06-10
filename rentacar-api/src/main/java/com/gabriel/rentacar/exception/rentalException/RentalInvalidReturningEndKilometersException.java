package com.gabriel.rentacar.exception.rentalException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class RentalInvalidReturningEndKilometersException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public RentalInvalidReturningEndKilometersException(Long id) {
		super(String.format("Invalid end kilometers for rent %d",id),"End kilometers must be above start kilometers");
	}
}
