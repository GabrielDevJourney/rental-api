package com.gabriel.rentacar.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

	private int status;
	private String error;
	private String message;
	private LocalDateTime timestamp;
}
