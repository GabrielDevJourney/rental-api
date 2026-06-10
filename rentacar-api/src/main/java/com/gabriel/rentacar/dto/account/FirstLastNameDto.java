package com.gabriel.rentacar.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirstLastNameDto {

	@Schema(example = "Jane")
	private String firstName;

	@Schema(example = "Smith")
	private String lastName;
}
