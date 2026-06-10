package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleInvalidDataException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleInvalidDataException(String field,String message) {
      super(String.format("Invalid vehicle data: %s - %s", field, message),
              String.format("Please provide valid %s information", field));
	}
}
