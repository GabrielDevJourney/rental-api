package com.gabriel.rentacar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.rentacar.dto.account.AccountDto;
import com.gabriel.rentacar.dto.auth.AuthRequestDto;
import com.gabriel.rentacar.dto.vehicle.VehicleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VehicleControllerTest {

	@Autowired
	@SuppressWarnings("unused")
	private WebApplicationContext webApplicationContext;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;
	private String adminToken;

	private static final String VEHICLES_URL = "/api/vehicles";
	private static final String VEHICLE_ADMIN_EMAIL = "vehicletest@example.com";
	private static final String VEHICLE_ADMIN_PASSWORD = "P@ssw0rd123!";
	private static final String TEST_PLATE = "AA-00-00";

	@BeforeEach
	void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		adminToken = loginAndGetToken();
	}

	@Test
	void when_GetAllVehicles_without_Token_then_Returns403() throws Exception {
		mockMvc.perform(get(VEHICLES_URL))
				.andExpect(status().isForbidden());
	}

	@Test
	void when_GetAllVehicles_with_ValidToken_then_Returns200() throws Exception {
		mockMvc.perform(get(VEHICLES_URL)
						.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content").isArray());
	}

	@Test
	void when_GetAllVehicles_with_PaginationParams_then_Returns200() throws Exception {
		mockMvc.perform(get(VEHICLES_URL)
						.param("page", "0")
						.param("size", "5")
						.param("sort", "id")
						.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.pageable.pageSize").value(5))
				.andExpect(jsonPath("$.data.pageable.pageNumber").value(0));
	}

	@Test
	void when_CreateVehicle_with_UserRole_then_Returns403() throws Exception {
		VehicleDto vehicleDto = buildValidVehicleDto();

		mockMvc.perform(post(VEHICLES_URL)
						.header("Authorization", "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(vehicleDto)))
				.andExpect(status().isForbidden());
	}

	// ── helpers ──────────────────────────────────────────────────────────────

	private String loginAndGetToken() throws Exception {
		AccountDto accountDto = new AccountDto();
		accountDto.setFirstName("Vehicle");
		accountDto.setLastName("Tester");
		accountDto.setEmail(VEHICLE_ADMIN_EMAIL);
		accountDto.setPassword(VEHICLE_ADMIN_PASSWORD);
		accountDto.setPhoneNumber("913456789");
		accountDto.setAge(30);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto)));

		String confirmBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", VEHICLE_ADMIN_EMAIL, VEHICLE_ADMIN_PASSWORD);
		mockMvc.perform(patch("/api/accounts/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(confirmBody));

		AuthRequestDto loginRequest = new AuthRequestDto();
		loginRequest.setEmail(VEHICLE_ADMIN_EMAIL);
		loginRequest.setPassword(VEHICLE_ADMIN_PASSWORD);

		MvcResult result = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andReturn();

		String responseBody = result.getResponse().getContentAsString();
		return objectMapper.readTree(responseBody).get("data").get("token").asText();
	}

	private VehicleDto buildValidVehicleDto() {
		VehicleDto dto = new VehicleDto();
		dto.setPlate(TEST_PLATE);
		dto.setBrand("Ferrari");
		dto.setModel("F8");
		dto.setColor("red");
		dto.setYearManufacture(2022);
		dto.setDailyRate(new BigDecimal("500.00"));
		dto.setCurrentKilometers(0);
		dto.setMaintenanceKilometers(5000);
		return dto;
	}
}
