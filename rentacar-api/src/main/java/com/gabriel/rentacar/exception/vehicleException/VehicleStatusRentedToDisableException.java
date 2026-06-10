package com.gabriel.rentacar.exception.vehicleException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class VehicleStatusRentedToDisableException extends ValidationException {
	@Serial
	private static final long serialVersionUID = 1L;

	public VehicleStatusRentedToDisableException(Long vehicleId) {
		super(String.format("Can't set status of disable to rented vehicle of ID: %d",vehicleId), "Can't make status " +
				"of " +
				"rented become" +
				" disable");
	}
}
