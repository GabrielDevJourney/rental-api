package com.gabriel.rentacar.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@SuppressWarnings({"unused", "NullableProblems"})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
		return ResponseEntity.ok(new ApiResponse<>(true, message, data, LocalDateTime.now()));
	}

	public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(true, message, data, LocalDateTime.now()));
	}
}
