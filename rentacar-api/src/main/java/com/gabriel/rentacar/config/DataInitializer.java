package com.gabriel.rentacar.config;

import com.gabriel.rentacar.entity.AccountEntity;
import com.gabriel.rentacar.entity.RentalEntity;
import com.gabriel.rentacar.entity.VehicleEntity;
import com.gabriel.rentacar.enums.RentalStatus;
import com.gabriel.rentacar.enums.UserRole;
import com.gabriel.rentacar.enums.VehicleStatus;
import com.gabriel.rentacar.repository.AccountRepository;
import com.gabriel.rentacar.repository.RentalRepository;
import com.gabriel.rentacar.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

	private final AccountRepository accountRepository;
	private final VehicleRepository vehicleRepository;
	private final RentalRepository rentalRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public DataInitializer(AccountRepository accountRepository,
	                       VehicleRepository vehicleRepository,
	                       RentalRepository rentalRepository,
	                       BCryptPasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.vehicleRepository = vehicleRepository;
		this.rentalRepository = rentalRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@SuppressWarnings("NullableProblems")
	public void run(String... args) {
		if (accountRepository.count() > 0) return;

		// --- Accounts ---
		accountRepository.save(buildAccount(
				"Admin", "admin@rentacar.com",
				"Admin@1234!", "911000001", 35, UserRole.ADMIN, true));

		AccountEntity manager = accountRepository.save(buildAccount(
				"Manager", "manager@rentacar.com",
				"Manager@1234!", "921000002", 30, UserRole.MANAGER, true));

		AccountEntity user = accountRepository.save(buildAccount(
				"Regular", "user@rentacar.com",
				"User@1234!", "931000003", 25, UserRole.USER, true));

		// Inactive — for testing AccountNotActiveException
		accountRepository.save(buildAccount(
				"Inactive", "inactive@rentacar.com",
				"Inactive@1234!", "961000004", 22, UserRole.USER, false));

		// --- Vehicles ---
		VehicleEntity ferrari = vehicleRepository.save(buildVehicle(
				"Ferrari", "F8 Tributo", "Red", 2022,
				"AA-00-BB", new BigDecimal("850.00"), VehicleStatus.AVAILABLE, 5000));

		vehicleRepository.save(buildVehicle(
				"Lamborghini", "Huracan", "Yellow", 2023,
				"BB-11-CC", new BigDecimal("950.00"), VehicleStatus.AVAILABLE, 3200));

		VehicleEntity porsche = buildVehicle(
				"Porsche", "911 GT3", "White", 2021,
				"CC-22-DD", new BigDecimal("650.00"), VehicleStatus.MAINTENANCE, 15000);
		porsche.setMaintenanceEndDate(LocalDate.now().plusDays(5));
		vehicleRepository.save(porsche);

		VehicleEntity bentley = vehicleRepository.save(buildVehicle(
				"Bentley", "Continental GT", "Black", 2022,
				"DD-33-EE", new BigDecimal("750.00"), VehicleStatus.RENTED, 8000));

		vehicleRepository.save(buildVehicle(
				"Aston Martin", "DB11", "Silver", 2020,
				"EE-44-FF", new BigDecimal("700.00"), VehicleStatus.AVAILABLE, 12000));

		// --- Rentals ---

		// Completed — ferrari / user, 10 days ago
		RentalEntity completed = new RentalEntity();
		completed.setAccountEntity(user);
		completed.setVehicleEntity(ferrari);
		completed.setDateStart(LocalDate.now().minusDays(10));
		completed.setDateEnd(LocalDate.now().minusDays(5));
		completed.setDateReturn(LocalDate.now().minusDays(5));
		completed.setStartKilometers(4800);
		completed.setEndKilometers(5000);
		completed.setStatus(RentalStatus.COMPLETED);
		completed.setTotalPrice(new BigDecimal("4250.00"));
		rentalRepository.save(completed);

		// Active — bentley / manager, due in 7 days
		RentalEntity active = new RentalEntity();
		active.setAccountEntity(manager);
		active.setVehicleEntity(bentley);
		active.setDateStart(LocalDate.now());
		active.setDateEnd(LocalDate.now().plusDays(7));
		active.setStartKilometers(8000);
		active.setStatus(RentalStatus.ACTIVE);
		rentalRepository.save(active);
	}

	private AccountEntity buildAccount(String firstName, String email,
	                                   String rawPassword, String phone, int age,
	                                   UserRole role, boolean active) {
		AccountEntity a = new AccountEntity();
		a.setFirstName(firstName);
		a.setLastName("User");
		a.setEmail(email);
		a.setPassword(passwordEncoder.encode(rawPassword));
		a.setPhoneNumber(phone);
		a.setAge(age);
		a.setUserRole(role);
		a.setActive(active);
		return a;
	}

	private VehicleEntity buildVehicle(String brand, String model, String color,
	                                   int year, String plate, BigDecimal dailyRate,
	                                   VehicleStatus status, int km) {
		VehicleEntity v = new VehicleEntity();
		v.setBrand(brand);
		v.setModel(model);
		v.setColor(color);
		v.setYearManufacture(year);
		v.setPlate(plate);
		v.setDailyRate(dailyRate);
		v.setStatus(status);
		v.setCurrentKilometers(km);
		return v;
	}
}
