package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleInvalidPlateFormatException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleInvalidPlateFormatException(String plate,String message) {
		super(String.format("Invalid vehicle plate: %s - %s", plate, message),
				String.format(plate + " " + message));	}
}
