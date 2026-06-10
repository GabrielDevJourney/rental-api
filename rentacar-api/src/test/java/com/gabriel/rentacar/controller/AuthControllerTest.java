package com.gabriel.rentacar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.rentacar.dto.account.AccountDto;

import com.gabriel.rentacar.dto.auth.AuthRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

	@Autowired
	@SuppressWarnings("unused")
	private WebApplicationContext webApplicationContext;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	private static final String REGISTER_URL = "/api/auth/register";
	private static final String LOGIN_URL = "/api/auth/login";
	private static final String LOGIN_EMAIL = "login.test@example.com";
	private static final String LOGIN_PASSWORD = "P@ssw0rd123!";

	@Test
	void when_RegisterWithValidData_then_Returns201() throws Exception {
		AccountDto accountDto = buildValidAccountDto("test.user@example.com");

		mockMvc.perform(post(REGISTER_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(accountDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Account registered successfully"));
	}

	@Test
	void when_RegisterWithDuplicateEmail_then_Returns400() throws Exception {
		AccountDto accountDto = buildValidAccountDto("duplicate@example.com");

		mockMvc.perform(post(REGISTER_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto)));

		mockMvc.perform(post(REGISTER_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(accountDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_RegisterWithMissingFields_then_Returns400() throws Exception {
		AccountDto incomplete = new AccountDto();
		incomplete.setEmail("missing@example.com");

		mockMvc.perform(post(REGISTER_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(incomplete)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_LoginWithValidCredentials_then_ReturnsTokenInResponse() throws Exception {
		registerAndConfirm();

		AuthRequestDto loginRequest = new AuthRequestDto();
		loginRequest.setEmail(LOGIN_EMAIL);
		loginRequest.setPassword(LOGIN_PASSWORD);

		mockMvc.perform(post(LOGIN_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.token").isNotEmpty())
				.andExpect(jsonPath("$.data.email").value(LOGIN_EMAIL));
	}

	@Test
	void when_LoginWithWrongPassword_then_Returns400() throws Exception {
		AccountDto accountDto = buildValidAccountDto("wrongpass@example.com");
		mockMvc.perform(post(REGISTER_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto)));

		AuthRequestDto loginRequest = new AuthRequestDto();
		loginRequest.setEmail("wrongpass@example.com");
		loginRequest.setPassword("WrongPassword999!");

		mockMvc.perform(post(LOGIN_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_LoginWithNonExistentEmail_then_Returns400() throws Exception {
		AuthRequestDto loginRequest = new AuthRequestDto();
		loginRequest.setEmail("ghost@example.com");
		loginRequest.setPassword("P@ssw0rd123!");

		mockMvc.perform(post(LOGIN_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isBadRequest());
	}

	// ── helpers ──────────────────────────────────────────────────────────────

	private AccountDto buildValidAccountDto(String email) {
		AccountDto dto = new AccountDto();
		dto.setFirstName("Test");
		dto.setLastName("User");
		dto.setEmail(email);
		dto.setPassword("P@ssw0rd123!");
		dto.setPhoneNumber("912345678");
		dto.setAge(25);
		return dto;
	}

	private void registerAndConfirm() throws Exception {
		AccountDto accountDto = buildValidAccountDto(LOGIN_EMAIL);
		mockMvc.perform(post(REGISTER_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountDto)));

		String confirmBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", LOGIN_EMAIL, LOGIN_PASSWORD);
		mockMvc.perform(patch("/api/accounts/confirm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(confirmBody));
	}
}
