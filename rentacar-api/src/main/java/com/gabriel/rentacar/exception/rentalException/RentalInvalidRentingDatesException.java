package com.gabriel.rentacar.exception.rentalException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class RentalInvalidRentingDatesException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public RentalInvalidRentingDatesException(String messageLog, String clientMessage){
		super(messageLog,clientMessage);
	}
}
