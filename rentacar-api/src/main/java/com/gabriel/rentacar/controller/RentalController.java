package com.gabriel.rentacar.controller;

import com.gabriel.rentacar.dto.common.ApiResponse;
import com.gabriel.rentacar.dto.rent.RentalRequestDto;
import com.gabriel.rentacar.dto.rent.RentalResponseDto;
import com.gabriel.rentacar.enums.RentalStatus;
import com.gabriel.rentacar.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@SuppressWarnings({"unused", "NullableProblems"})
public class RentalController {

	private final RentalService rentalService;

	public RentalController(RentalService rentalService) {
		this.rentalService = rentalService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createRenting(@Valid @RequestBody RentalRequestDto rentalRequestDto) {
		rentalService.createRenting(rentalRequestDto);
		return ApiResponse.created("Rental created", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@PatchMapping("/return/{id}/{endKilometers}")
	public ResponseEntity<ApiResponse<Void>> endRenting(
			@PathVariable Long id,
			@PathVariable int endKilometers) {
		rentalService.endRenting(id, endKilometers);
		return ApiResponse.ok("Rental completed", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<RentalResponseDto>> getRentingInfo(@PathVariable Long id) {
		return ApiResponse.ok("Rental retrieved", rentalService.getRentingInfo(id));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/vehicle/{id}")
	public ResponseEntity<ApiResponse<RentalResponseDto>> getRentingInfoByVehicleId(@PathVariable Long id) {
		return ApiResponse.ok("Rental retrieved", rentalService.getRentingInfoByVehicleId(id));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/account/{id}")
	public ResponseEntity<ApiResponse<RentalResponseDto>> getRentingInfoByAccountId(@PathVariable Long id) {
		return ApiResponse.ok("Rental retrieved", rentalService.getRentingInfoByAccountId(id));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/vehicle/{id}/{status}")
	public ResponseEntity<ApiResponse<RentalResponseDto>> getRentingInfoByVehicleIdAndStatus(
			@PathVariable Long id,
			@PathVariable RentalStatus status) {
		return ApiResponse.ok("Rental retrieved", rentalService.getRentingInfoByVehicleIdAndStatus(id, status));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/account/{id}/{status}")
	public ResponseEntity<ApiResponse<RentalResponseDto>> getRentingInfoByAccountIdAndStatus(
			@PathVariable Long id,
			@PathVariable RentalStatus status) {
		return ApiResponse.ok("Rental retrieved", rentalService.getRentingInfoByAccountIdAndStatus(id, status));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/account/all/{id}")
	public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getRentalsOfAccount(@PathVariable Long id) {
		List<RentalResponseDto> rentals = rentalService.getAllRentalsForAccount(id);
		return ApiResponse.ok("Rentals retrieved", rentals);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/vehicle/all/{id}")
	public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getRentalsOfVehicle(@PathVariable Long id) {
		List<RentalResponseDto> rentals = rentalService.getAllRentalsForVehicle(id);
		return ApiResponse.ok("Rentals retrieved", rentals);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/all/{status}")
	public ResponseEntity<ApiResponse<List<RentalResponseDto>>> getRentalsOfStatus(@PathVariable RentalStatus status) {
		List<RentalResponseDto> rentals = rentalService.getAllRentalsOfStatus(status);
		return ApiResponse.ok("Rentals retrieved", rentals);
	}
}
