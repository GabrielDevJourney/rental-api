package com.gabriel.rentacar.service;

import com.gabriel.rentacar.dto.rent.RentalRequestDto;
import com.gabriel.rentacar.dto.rent.RentalResponseDto;
import com.gabriel.rentacar.entity.AccountEntity;
import com.gabriel.rentacar.entity.RentalEntity;
import com.gabriel.rentacar.entity.VehicleEntity;
import com.gabriel.rentacar.enums.RentalStatus;
import com.gabriel.rentacar.enums.VehicleStatus;
import com.gabriel.rentacar.exception.accountException.AccountNotActiveException;
import com.gabriel.rentacar.exception.accountException.AccountNotFoundException;
import com.gabriel.rentacar.exception.rentalException.RentalInvalidReturningEndKilometersException;
import com.gabriel.rentacar.exception.rentalException.RentalNotFoundException;
import com.gabriel.rentacar.exception.vehicleException.VehicleNotFoundException;
import com.gabriel.rentacar.mapper.RentalMapper;
import com.gabriel.rentacar.repository.AccountRepository;
import com.gabriel.rentacar.repository.RentalRepository;
import com.gabriel.rentacar.repository.VehicleRepository;
import com.gabriel.rentacar.utils.DateValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

	@Mock
	private RentalRepository rentalRepository;

	@Mock
	private RentalMapper rentalMapper;

	@Mock
	private VehicleRepository vehicleRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private VehicleService vehicleService;

	@Mock
	private DateValidation dateValidator;

	@InjectMocks
	private RentalService rentalService;

	private AccountEntity activeAccountEntity;
	private AccountEntity inactiveAccountEntity;
	private VehicleEntity vehicleEntity;
	private RentalEntity activeRentalEntity;
	private RentalEntity completedRentalEntity;
	private RentalRequestDto rentalRequestDto;
	private RentalResponseDto rentalResponseDto;

	@BeforeEach
	void setUp() {
		activeAccountEntity = new AccountEntity();
		activeAccountEntity.setId(1L);
		activeAccountEntity.setFirstName("John");
		activeAccountEntity.setLastName("Doe");
		activeAccountEntity.setEmail("john.doe@example.com");
		activeAccountEntity.setActive(true);

		inactiveAccountEntity = new AccountEntity();
		inactiveAccountEntity.setId(2L);
		inactiveAccountEntity.setFirstName("Jane");
		inactiveAccountEntity.setLastName("Smith");
		inactiveAccountEntity.setEmail("jane.smith@example.com");
		inactiveAccountEntity.setActive(false);

		vehicleEntity = new VehicleEntity();
		vehicleEntity.setId(1L);
		vehicleEntity.setPlate("AA-11-BB");
		vehicleEntity.setBrand("TOYOTA");
		vehicleEntity.setModel("COROLLA");
		vehicleEntity.setStatus(VehicleStatus.AVAILABLE);
		vehicleEntity.setCurrentKilometers(1000);
		vehicleEntity.setDailyRate(new BigDecimal("50.00"));

		activeRentalEntity = new RentalEntity();
		activeRentalEntity.setId(1L);
		activeRentalEntity.setAccountEntity(activeAccountEntity);
		activeRentalEntity.setVehicleEntity(vehicleEntity);
		activeRentalEntity.setDateStart(LocalDate.now().minusDays(5));
		activeRentalEntity.setDateEnd(LocalDate.now().plusDays(2));
		activeRentalEntity.setStartKilometers(1000);
		activeRentalEntity.setStatus(RentalStatus.ACTIVE);

		completedRentalEntity = new RentalEntity();
		completedRentalEntity.setId(2L);
		completedRentalEntity.setAccountEntity(activeAccountEntity);
		completedRentalEntity.setVehicleEntity(vehicleEntity);
		completedRentalEntity.setDateStart(LocalDate.now().minusDays(10));
		completedRentalEntity.setDateEnd(LocalDate.now().minusDays(5));
		completedRentalEntity.setDateReturn(LocalDate.now().minusDays(5));
		completedRentalEntity.setStartKilometers(1000);
		completedRentalEntity.setEndKilometers(1500);
		completedRentalEntity.setTotalPrice(new BigDecimal("250.00"));
		completedRentalEntity.setStatus(RentalStatus.COMPLETED);

		rentalRequestDto = new RentalRequestDto();
		rentalRequestDto.setAccountId(1L);
		rentalRequestDto.setVehicleId(1L);
		rentalRequestDto.setDateStart(LocalDate.now().plusDays(1));
		rentalRequestDto.setDateEnd(LocalDate.now().plusDays(6));

		rentalResponseDto = new RentalResponseDto();
		rentalResponseDto.setId(1L);
		rentalResponseDto.setAccountId(1L);
		rentalResponseDto.setVehicleId(1L);
		rentalResponseDto.setDateStart(LocalDate.now());
		rentalResponseDto.setDateEnd(LocalDate.now().plusDays(5));
		rentalResponseDto.setRentalStatus(RentalStatus.ACTIVE);
	}

	// ========== CREATE RENTAL TESTS ==========

	@Test
	void when_CreatingRenting_then_Success() {
		when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicleEntity));
		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(activeAccountEntity));
		when(rentalMapper.toEntityRequest(any(RentalRequestDto.class))).thenReturn(activeRentalEntity);
		doNothing().when(dateValidator).validateRentalDates(anyLong(), any(LocalDate.class), any(LocalDate.class));

		rentalService.createRenting(rentalRequestDto);

		verify(vehicleService).updateVehicleStatusToRented(vehicleEntity);
		verify(rentalRepository).save(activeRentalEntity);
	}

	@Test
	void when_CreatingRenting_with_VehicleNotFound_then_ThrowsException() {
		when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(VehicleNotFoundException.class, () -> rentalService.createRenting(rentalRequestDto));
		verify(rentalRepository, never()).save(any());
	}

	@Test
	void when_CreatingRenting_with_AccountNotFound_then_ThrowsException() {
		when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicleEntity));
		when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(AccountNotFoundException.class, () -> rentalService.createRenting(rentalRequestDto));
		verify(rentalRepository, never()).save(any());
	}

	@Test
	void when_CreatingRenting_with_AccountNotActive_then_ThrowsException() {
		when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicleEntity));
		when(accountRepository.findById(anyLong())).thenReturn(Optional.of(inactiveAccountEntity));

		assertThrows(AccountNotActiveException.class, () -> rentalService.createRenting(rentalRequestDto));
		verify(rentalRepository, never()).save(any());
	}

	// ========== END RENTAL TESTS ==========

	@Test
	void when_EndingRenting_then_Success() {
		when(rentalRepository.findByIdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.of(activeRentalEntity));

		rentalService.endRenting(1L, 1500);

		assertEquals(RentalStatus.COMPLETED, activeRentalEntity.getStatus());
		assertEquals(1500, activeRentalEntity.getEndKilometers());
		assertNotNull(activeRentalEntity.getDateReturn());
		assertNotNull(activeRentalEntity.getTotalPrice());
		assertTrue(activeRentalEntity.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);

		verify(vehicleService).completeRental(vehicleEntity, 1000, 1500);
		verify(rentalRepository).save(activeRentalEntity);
	}

	@Test
	void when_EndingRenting_with_RentalNotFound_then_ThrowsException() {
		when(rentalRepository.findByIdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.empty());

		assertThrows(RentalNotFoundException.class, () -> rentalService.endRenting(1L, 1500));
		verify(rentalRepository, never()).save(any());
	}

	@Test
	void when_EndingRenting_with_InvalidEndKilometers_then_ThrowsException() {
		when(rentalRepository.findByIdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.of(activeRentalEntity));

		assertThrows(RentalInvalidReturningEndKilometersException.class, () -> rentalService.endRenting(1L, 900));
		verify(rentalRepository, never()).save(any());
	}

	// ========== GET RENTAL INFO TESTS ==========

	@Test
	void when_GettingRentingInfo_then_Success() {
		when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(activeRentalEntity));
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		RentalResponseDto result = rentalService.getRentingInfo(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals(RentalStatus.ACTIVE, result.getRentalStatus());
	}

	@Test
	void when_GettingRentingInfo_with_NotFound_then_ThrowsException() {
		when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(RentalNotFoundException.class, () -> rentalService.getRentingInfo(1L));
	}

	@Test
	void when_GettingRentingInfoByVehicleId_then_Success() {
		when(rentalRepository.findByVehicleEntity_Id(anyLong())).thenReturn(Optional.of(activeRentalEntity));
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		RentalResponseDto result = rentalService.getRentingInfoByVehicleId(1L);

		assertNotNull(result);
		assertEquals(1L, result.getVehicleId());
	}

	@Test
	void when_GettingRentingInfoByAccountId_then_Success() {
		when(rentalRepository.findByAccountEntity_Id(anyLong())).thenReturn(Optional.of(activeRentalEntity));
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		RentalResponseDto result = rentalService.getRentingInfoByAccountId(1L);

		assertNotNull(result);
		assertEquals(1L, result.getAccountId());
	}

	@Test
	void when_GettingRentingInfoByVehicleIdAndStatus_then_Success() {
		when(rentalRepository.findByVehicleEntity_IdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.of(activeRentalEntity));
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		RentalResponseDto result = rentalService.getRentingInfoByVehicleIdAndStatus(1L, RentalStatus.ACTIVE);

		assertNotNull(result);
		assertEquals(1L, result.getVehicleId());
	}

	@Test
	void when_GettingRentingInfoByVehicleIdAndStatus_with_NotFound_then_ThrowsRentalNotFoundException() {
		when(rentalRepository.findByVehicleEntity_IdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.empty());

		assertThrows(RentalNotFoundException.class,
				() -> rentalService.getRentingInfoByVehicleIdAndStatus(1L, RentalStatus.ACTIVE));
	}

	@Test
	void when_GettingRentingInfoByAccountIdAndStatus_then_Success() {
		when(rentalRepository.findByAccountEntity_IdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.of(activeRentalEntity));
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		RentalResponseDto result = rentalService.getRentingInfoByAccountIdAndStatus(1L, RentalStatus.ACTIVE);

		assertNotNull(result);
		assertEquals(1L, result.getAccountId());
	}

	@Test
	void when_GettingRentingInfoByAccountIdAndStatus_with_NotFound_then_ThrowsRentalNotFoundException() {
		when(rentalRepository.findByAccountEntity_IdAndStatus(anyLong(), eq(RentalStatus.ACTIVE)))
				.thenReturn(Optional.empty());

		assertThrows(RentalNotFoundException.class,
				() -> rentalService.getRentingInfoByAccountIdAndStatus(1L, RentalStatus.ACTIVE));
	}

	// ========== GET ALL RENTALS TESTS ==========

	@Test
	void when_GettingAllRentalsForAccount_then_Success() {
		List<RentalEntity> accountRentals = Arrays.asList(activeRentalEntity, completedRentalEntity);
		when(rentalRepository.findAllByAccountEntity_Id(anyLong())).thenReturn(accountRentals);
		when(rentalMapper.toDtoResponse(activeRentalEntity)).thenReturn(rentalResponseDto);
		when(rentalMapper.toDtoResponse(completedRentalEntity)).thenReturn(rentalResponseDto);

		List<RentalResponseDto> results = rentalService.getAllRentalsForAccount(1L);

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	void when_GettingAllRentalsForVehicle_then_Success() {
		List<RentalEntity> vehicleRentals = Arrays.asList(activeRentalEntity, completedRentalEntity);
		when(rentalRepository.findAllByVehicleEntity_Id(anyLong())).thenReturn(vehicleRentals);
		when(rentalMapper.toDtoResponse(activeRentalEntity)).thenReturn(rentalResponseDto);
		when(rentalMapper.toDtoResponse(completedRentalEntity)).thenReturn(rentalResponseDto);

		List<RentalResponseDto> results = rentalService.getAllRentalsForVehicle(1L);

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	void when_GettingAllRentalsOfStatus_then_Success() {
		List<RentalEntity> statusRentals = Arrays.asList(activeRentalEntity, activeRentalEntity);
		when(rentalRepository.findAllByStatus(eq(RentalStatus.ACTIVE))).thenReturn(statusRentals);
		when(rentalMapper.toDtoResponse(any(RentalEntity.class))).thenReturn(rentalResponseDto);

		List<RentalResponseDto> results = rentalService.getAllRentalsOfStatus(RentalStatus.ACTIVE);

		assertNotNull(results);
		assertEquals(2, results.size());
	}
}
