package com.gabriel.rentacar.dto.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

	@Schema(example = "FF-55-GG")
	@NotBlank(message = "Plate is required")
	private String plate;

	@Schema(example = "McLaren")
	@NotBlank(message = "Brand is required")
	@Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
	@Pattern(regexp = "^[A-Za-z ]+$", message = "Brand can only contain letters and spaces")
	private String brand;

	@Schema(example = "720S")
	@Size(max = 50, message = "Model must be up to 50 characters")
	@Pattern(regexp = "^[A-Za-z0-9 -]*$", message = "Model can only contain letters, numbers, spaces, and hyphens")
	private String model;

	@Schema(example = "Orange")
	@Size(max = 30, message = "Color must be up to 30 characters")
	@Pattern(regexp = "^[A-Za-z ]*$", message = "Color can only contain letters and spaces")
	private String color;

	@Schema(example = "2023")
	@NotNull(message = "Year of manufacture required")
	private int yearManufacture;

	@Schema(example = "800.00")
	@DecimalMin(value = "20.00", message = "Daily rate must be at least 20")
	@DecimalMax(value = "10000.00", message = "Daily rate must not exceed 10000")
	private BigDecimal dailyRate;

	@Schema(example = "1500")
	@Min(value = 0, message = "Kilometers must be positive")
	private int currentKilometers;

	@Schema(example = "0")
	@Min(value = 5000, message = "Maintenance kilometers must be at least 5000km")
	@Max(value = 10000, message = "Maintenance kilometers must not exceed 10000km")
	private int maintenanceKilometers;
}
