package com.gabriel.rentacar.dto.rent;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequestDto {

	@Schema(example = "3")
	@NotNull(message = "Account ID is required")
	private Long accountId;

	@Schema(example = "2")
	@NotNull(message = "Vehicle ID is required")
	private Long vehicleId;

	@Schema(example = "2026-12-01")
	@NotNull(message = "Start date is required")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateStart;

	@Schema(example = "2026-12-05")
	@NotNull(message = "End date is required")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateEnd;
}
