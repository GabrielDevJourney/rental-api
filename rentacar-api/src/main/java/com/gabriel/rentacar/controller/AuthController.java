package com.gabriel.rentacar.controller;

import com.gabriel.rentacar.dto.account.AccountDto;
import com.gabriel.rentacar.dto.auth.AuthRequestDto;
import com.gabriel.rentacar.dto.auth.AuthResponseDto;
import com.gabriel.rentacar.dto.auth.ChangePasswordDto;
import com.gabriel.rentacar.dto.common.ApiResponse;
import com.gabriel.rentacar.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@SuppressWarnings({"unused", "NullableProblems"})
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AccountDto accountDto) {
		authService.registerAccount(accountDto);
		return ApiResponse.created("Account registered successfully", null);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto authRequest) {
		AuthResponseDto response = authService.authenticate(authRequest);
		return ApiResponse.ok("Login successful", response);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#accountId)")
	@PatchMapping("/change-password/{accountId}")
	public ResponseEntity<ApiResponse<Void>> changePassword(
			@PathVariable Long accountId,
			@Valid @RequestBody ChangePasswordDto passwordDto) {
		authService.changePassword(accountId, passwordDto);
		return ApiResponse.ok("Password changed successfully", null);
	}
}
