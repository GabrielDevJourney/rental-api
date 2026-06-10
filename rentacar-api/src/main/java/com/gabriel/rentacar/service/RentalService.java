package com.gabriel.rentacar.service;

import com.gabriel.rentacar.dto.rent.RentalRequestDto;
import com.gabriel.rentacar.dto.rent.RentalResponseDto;
import com.gabriel.rentacar.entity.AccountEntity;
import com.gabriel.rentacar.entity.RentalEntity;
import com.gabriel.rentacar.entity.VehicleEntity;
import com.gabriel.rentacar.enums.RentalStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class RentalService {
	private final RentalRepository rentalRepository;
	private final RentalMapper rentalMapper;
	private final VehicleRepository vehicleRepository;
	private final AccountRepository accountRepository;
	private final VehicleService vehicleService;
	private final DateValidation dateValidator;

	public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper, VehicleRepository vehicleRepository,
	                     AccountRepository accountRepository, VehicleService vehicleService, DateValidation dateValidator) {
		this.rentalRepository = rentalRepository;
		this.rentalMapper = rentalMapper;
		this.vehicleRepository = vehicleRepository;
		this.accountRepository = accountRepository;
		this.vehicleService = vehicleService;
		this.dateValidator = dateValidator;
	}

	@Transactional
	public void createRenting(RentalRequestDto rentalRequestDto) {
		Long vehicleId = rentalRequestDto.getVehicleId();
		Long accountId = rentalRequestDto.getAccountId();

		VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
				.orElseThrow(() -> new VehicleNotFoundException(vehicleId));
		AccountEntity account = accountRepository.findById(accountId)
				.orElseThrow(() -> new AccountNotFoundException(accountId));

		validateAccountIsActive(account);

		dateValidator.validateRentalDates(
				rentalRequestDto.getVehicleId(),
				rentalRequestDto.getDateStart(),
				rentalRequestDto.getDateEnd()
		);

		vehicleService.updateVehicleStatusToRented(vehicle);

		RentalEntity rentalEntity = rentalMapper.toEntityRequest(rentalRequestDto);
		rentalEntity.setAccountEntity(account);
		rentalEntity.setVehicleEntity(vehicle);
		rentalEntity.setStartKilometers(vehicle.getCurrentKilometers());

		rentalRepository.save(rentalEntity);
	}

	@Transactional
	public void endRenting(Long id, int rentalReturnKilometers) {
		RentalEntity rental = findActiveRentalById(id);

		validateReturnKilometers(rental, rentalReturnKilometers);

		LocalDate returnDate = LocalDate.now();
		rental.setEndKilometers(rentalReturnKilometers);
		rental.setDateReturn(returnDate);
		rental.setStatus(RentalStatus.COMPLETED);

		BigDecimal totalPrice = calculateRentFinalPrice(
				rental.getVehicleEntity().getDailyRate(),
				rental.getDateStart(),
				returnDate
		);
		rental.setTotalPrice(totalPrice);

		vehicleService.completeRental(
				rental.getVehicleEntity(),
				rental.getStartKilometers(),
				rentalReturnKilometers
		);

		rentalRepository.save(rental);
	}

	public RentalResponseDto getRentingInfo(Long id) {
		RentalEntity rent = rentalRepository.findById(id)
				.orElseThrow(() -> new RentalNotFoundException(id));
		return rentalMapper.toDtoResponse(rent);
	}

	public RentalResponseDto getRentingInfoByVehicleId(Long id) {
		RentalEntity rent = rentalRepository.findByVehicleEntity_Id(id)
				.orElseThrow(() -> new RentalNotFoundException(id));
		return rentalMapper.toDtoResponse(rent);
	}

	public RentalResponseDto getRentingInfoByAccountId(Long id) {
		RentalEntity rent = rentalRepository.findByAccountEntity_Id(id)
				.orElseThrow(() -> new RentalNotFoundException(id));
		return rentalMapper.toDtoResponse(rent);
	}

	public RentalResponseDto getRentingInfoByVehicleIdAndStatus(Long id, RentalStatus status) {
		return rentalRepository.findByVehicleEntity_IdAndStatus(id, status)
				.map(rentalMapper::toDtoResponse)
				.orElseThrow(() -> new RentalNotFoundException(id));
	}

	public RentalResponseDto getRentingInfoByAccountIdAndStatus(Long id, RentalStatus status) {
		return rentalRepository.findByAccountEntity_IdAndStatus(id, status)
				.map(rentalMapper::toDtoResponse)
				.orElseThrow(() -> new RentalNotFoundException(id));
	}

	public List<RentalResponseDto> getAllRentalsForAccount(Long id) {
		return rentalRepository.findAllByAccountEntity_Id(id).stream()
				.map(rentalMapper::toDtoResponse)
				.toList();
	}

	public List<RentalResponseDto> getAllRentalsForVehicle(Long id) {
		return rentalRepository.findAllByVehicleEntity_Id(id).stream()
				.map(rentalMapper::toDtoResponse)
				.toList();
	}

	public List<RentalResponseDto> getAllRentalsOfStatus(RentalStatus status) {
		return rentalRepository.findAllByStatus(status).stream()
				.map(rentalMapper::toDtoResponse)
				.toList();
	}

	//* PRIVATE HELPER METHODS

	private BigDecimal calculateRentFinalPrice(BigDecimal vehicleDailyPrice, LocalDate startDate, LocalDate endDate) {
		long days = startDate.until(endDate, ChronoUnit.DAYS);
		return vehicleDailyPrice.multiply(BigDecimal.valueOf(days));
	}

	private RentalEntity findActiveRentalById(Long id) {
		return rentalRepository.findByIdAndStatus(id, RentalStatus.ACTIVE)
				.orElseThrow(() -> new RentalNotFoundException(id));
	}

	private void validateReturnKilometers(RentalEntity rental, int returnKilometers) {
		if (returnKilometers <= rental.getStartKilometers()) {
			throw new RentalInvalidReturningEndKilometersException(rental.getId());
		}
	}

	private void validateAccountIsActive(AccountEntity account) {
		if (!account.isActive()) {
			throw new AccountNotActiveException(account.getId());
		}
	}
}
