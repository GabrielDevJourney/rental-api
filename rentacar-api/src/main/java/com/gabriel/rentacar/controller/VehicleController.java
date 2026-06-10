package com.gabriel.rentacar.controller;

import com.gabriel.rentacar.dto.common.ApiResponse;
import com.gabriel.rentacar.dto.vehicle.VehicleDto;
import com.gabriel.rentacar.enums.VehicleStatus;
import com.gabriel.rentacar.service.VehicleService;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@SuppressWarnings({"unused", "NullableProblems"})
public class VehicleController {

	private final VehicleService vehicleService;

	public VehicleController(VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/search/plate/{plate}")
	public ResponseEntity<ApiResponse<VehicleDto>> getVehicleByPlate(@PathVariable String plate) {
		VehicleDto vehicle = vehicleService.findByPlate(plate);
		if (vehicle == null) {
			return ResponseEntity.notFound().build();
		}
		return ApiResponse.ok("Vehicle retrieved", vehicle);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/search/id/{vehicleId}")
	public ResponseEntity<ApiResponse<VehicleDto>> getVehicleById(@PathVariable Long vehicleId) {
		return ApiResponse.ok("Vehicle retrieved", vehicleService.getVehicleById(vehicleId));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
	@GetMapping
	public ResponseEntity<ApiResponse<Page<VehicleDto>>> getAllVehicles(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String sort) {
		return ApiResponse.ok("Vehicles retrieved", vehicleService.getAllVehicles(page, size, sort));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
		vehicleService.createVehicle(vehicleDto);
		return ApiResponse.created("Vehicle created", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@Transactional
	@PatchMapping("/{vehicleId}/status/{status}")
	public ResponseEntity<ApiResponse<Void>> updateVehicleStatus(
			@PathVariable Long vehicleId,
			@PathVariable VehicleStatus status) {
		vehicleService.updateVehicleStatus(vehicleId, status);
		return ApiResponse.ok("Vehicle status updated", null);
	}
}
