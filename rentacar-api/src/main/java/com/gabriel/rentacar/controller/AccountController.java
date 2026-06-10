package com.gabriel.rentacar.controller;

import com.gabriel.rentacar.dto.account.AccountConfirmationDto;
import com.gabriel.rentacar.dto.account.AccountDto;
import com.gabriel.rentacar.dto.account.FirstLastNameDto;
import com.gabriel.rentacar.dto.common.ApiResponse;
import com.gabriel.rentacar.enums.UserRole;
import com.gabriel.rentacar.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@SuppressWarnings({"unused", "NullableProblems"})
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AccountDto>> getAccountById(@PathVariable Long id) {
		return ApiResponse.ok("Account retrieved", accountService.getAccountDtoById(id));
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/deactivated")
	public ResponseEntity<ApiResponse<List<AccountDto>>> getDeactivatedAccounts() {
		return ApiResponse.ok("Deactivated accounts retrieved", accountService.getDeactivatedAccounts());
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping("/deactivated/names")
	public ResponseEntity<ApiResponse<List<FirstLastNameDto>>> getDeactivatedAccountNames() {
		return ApiResponse.ok("Deactivated account names retrieved",
				accountService.getFirstNameAndLastNameAccountsThatAreDeactivated());
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	@GetMapping
	public ResponseEntity<ApiResponse<List<AccountDto>>> getAllAccounts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ApiResponse.ok("Accounts retrieved", accountService.getAllAccounts(page, size));
	}

	@PatchMapping("/confirm")
	public ResponseEntity<ApiResponse<Void>> confirmAccount(@RequestBody AccountConfirmationDto dto) {
		accountService.confirmAccount(dto.getEmail(), dto.getPassword());
		return ApiResponse.ok("Account confirmed", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<ApiResponse<Void>> deactivateAccount(@PathVariable Long id) {
		accountService.deactivateAccount(id);
		return ApiResponse.ok("Account deactivated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PatchMapping("/{id}/names")
	public ResponseEntity<ApiResponse<Void>> updateFirstNameAndLastName(
			@PathVariable Long id,
			@RequestBody FirstLastNameDto firstLastNameDto) {
		accountService.updateFirstNameAndLastName(id, firstLastNameDto);
		return ApiResponse.ok("Name updated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PatchMapping("/{id}/email")
	public ResponseEntity<ApiResponse<Void>> updateAccountEmail(
			@PathVariable Long id,
			@RequestBody @Email String email) {
		accountService.updateAccountEmail(id, email);
		return ApiResponse.ok("Email updated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PatchMapping("/{id}/age")
	public ResponseEntity<ApiResponse<Void>> updateAccountAge(
			@PathVariable Long id,
			@RequestBody @Range(min = 18, max = 99) Integer age) {
		accountService.updateAccountAge(id, age);
		return ApiResponse.ok("Age updated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PatchMapping("/{id}/phoneNumber")
	public ResponseEntity<ApiResponse<Void>> updateAccountPhoneNumber(
			@PathVariable Long id,
			@Valid @RequestBody String phoneNumber) {
		accountService.updateAccountPhoneNumber(id, phoneNumber);
		return ApiResponse.ok("Phone number updated", null);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{id}/role")
	public ResponseEntity<ApiResponse<Void>> updateAccountRole(
			@PathVariable Long id,
			@RequestBody UserRole role) {
		accountService.updateAccountRole(id, role);
		return ApiResponse.ok("Role updated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> updateFullAccountDetails(
			@PathVariable Long id,
			@Valid @RequestBody AccountDto accountDto) {
		accountService.updateFullAccountDetails(id, accountDto);
		return ApiResponse.ok("Account updated", null);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityUtils.isOwner(#id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
		accountService.deleteAccount(id);
		return ApiResponse.ok("Account deleted", null);
	}
}
