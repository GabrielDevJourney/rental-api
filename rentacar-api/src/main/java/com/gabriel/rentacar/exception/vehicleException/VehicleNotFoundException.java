package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ResourceNotFoundException;

import java.io.Serial;

public class VehicleNotFoundException extends ResourceNotFoundException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleNotFoundException(Long id) {
		super(String.format("Vehicle not found with id: %d", id));
	}
}