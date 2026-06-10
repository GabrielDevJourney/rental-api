package com.gabriel.rentacar.exception.rentalException;

import com.gabriel.rentacar.exception.ResourceNotFoundException;

import java.io.Serial;

public class RentalNotFoundException extends ResourceNotFoundException {
	@Serial
	private static final long serialVersionUID = 1L;

	public RentalNotFoundException(Long id) {
		super(String.format("Rental not found with ID: %d ",id));
	}
}
