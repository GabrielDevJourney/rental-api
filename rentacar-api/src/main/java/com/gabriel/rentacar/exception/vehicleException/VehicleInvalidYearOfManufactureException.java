package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleInvalidYearOfManufactureException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleInvalidYearOfManufactureException(int vehicleYear, int minYear, int maxYear) {
		super(String.format("%d is not a valid year", vehicleYear),
				String.format("Please insert a valid year between %d and %d",minYear,maxYear));
	}
}
