package com.gabriel.rentacar.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

	@Schema(example = "User@1234!")
	private String currentPassword;

	@Schema(example = "NewPass@5678!")
	private String newPassword;
}
