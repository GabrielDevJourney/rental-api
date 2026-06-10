package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleStatusDisableToMaintenanceException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleStatusDisableToMaintenanceException(Long id) {
		super(String.format("Trying to set maintenance to rented or disable for vehicle with ID: %d", id), "This " +
				"vehicle " +
				"can't be " +
				"send to maintenance!");
	}
}
