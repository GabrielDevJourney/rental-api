package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleLicensePlateAlreadyExistsException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleLicensePlateAlreadyExistsException(String plate) {
		super(String.format("License plate already exists: %s", plate), "Invalid plate!");
	}
}