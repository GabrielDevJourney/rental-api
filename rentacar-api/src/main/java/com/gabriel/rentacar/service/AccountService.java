package com.gabriel.rentacar.service;

import com.gabriel.rentacar.dto.account.AccountDto;
import com.gabriel.rentacar.dto.account.FirstLastNameDto;
import com.gabriel.rentacar.entity.AccountEntity;
import com.gabriel.rentacar.enums.UserRole;
import com.gabriel.rentacar.exception.ValidationException;
import com.gabriel.rentacar.exception.accountException.*;
import com.gabriel.rentacar.mapper.AccountMapper;
import com.gabriel.rentacar.repository.AccountRepository;
import com.gabriel.rentacar.utils.EmailValidation;
import com.gabriel.rentacar.utils.PasswordValidation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("unused")
@Service
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountMapper accountMapper;
	private final PasswordValidation passwordValidator;
	private final EmailValidation emailValidator;

	public AccountService(AccountRepository accountRepository, AccountMapper accountMapper,
	                      PasswordValidation passwordValidator, EmailValidation emailValidator) {
		this.accountRepository = accountRepository;
		this.accountMapper = accountMapper;
		this.passwordValidator = passwordValidator;
		this.emailValidator = emailValidator;
	}

	//REST ENDPOINTS
	@Transactional
	public void createAccount(AccountDto accountDto) {

		validateNotNull(accountDto, "account");

		validateName(accountDto.getFirstName(), "first name");
		validateName(accountDto.getLastName(), "last name");

		String accountEmail = validateEmailFormatAndNormalize(accountDto.getEmail());
		validateEmailUniqueness(accountEmail);

		String passwordForValidation = accountDto.getPassword();
		passwordValidator.validatePassword(passwordForValidation);

		String passwordEncrypted = passwordValidator.encryptPassword(passwordForValidation);

		Integer age = validateAge(accountDto.getAge(), accountEmail);

		String phoneNumber = validatePhoneNumberFormat(accountDto.getPhoneNumber());
		validatePhoneNumberUniqueness(phoneNumber);

		accountDto.setEmail(accountEmail);
		accountDto.setAge(age);
		accountDto.setPhoneNumber(phoneNumber);
		accountDto.setPassword(passwordEncrypted);

		AccountEntity accountEntity = accountMapper.toEntity(accountDto);
		accountEntity.setUserRole(UserRole.USER);
		accountRepository.save(accountEntity);
	}

	@Transactional
	public void confirmAccount(String email, String password) {
		AccountEntity account = accountRepository.findByEmail(email).orElseThrow(() -> new ValidationException("Not " +
				"account found", "Account not found"));

		if (passwordValidator.matches(password, account.getPassword())) {
			if (account.isActive()) {
				throw new AccountAlreadyActiveException(account.getId());
			} else {
				account.setActive(true);
			}
		}else{
			throw new AccountInvalidPasswordException("Passwords don't match at confirmation");
		}

		accountRepository.save(account);
	}

	@Transactional
	public void deactivateAccount(Long id) {
		AccountEntity account = getAccountEntityById(id);

		if (!account.isActive()) {
			throw new AccountAlreadyDeactivatedException(id);
		}

		account.setActive(false);
		accountRepository.save(account);
	}

	@Transactional
	public void deleteAccount(Long id) {
		AccountEntity account = getAccountEntityById(id);
		accountRepository.delete(account);
	}

	@Transactional
	public void updateFirstNameAndLastName(Long id, FirstLastNameDto firstLastNameDto) {
		validateNotNull(firstLastNameDto, "nameData");
		AccountEntity accountEntity = getAccountEntityById(id);

		validateName(firstLastNameDto.getFirstName(), "first name");
		accountEntity.setFirstName(firstLastNameDto.getFirstName());

		validateName(firstLastNameDto.getLastName(), "last name");
		accountEntity.setLastName(firstLastNameDto.getLastName());


		accountRepository.save(accountEntity);
	}

	@Transactional
	public void updateFullAccountDetails(Long id, AccountDto accountDto) {
		validateNotNull(accountDto, "account");
		AccountEntity accountEntity = getAccountEntityById(id);

		validateName(accountDto.getFirstName(), "first name");
		validateName(accountDto.getLastName(), "last name");

		String accountEmail = validateEmailFormatAndNormalize(accountDto.getEmail());

		//email exists or belongs to a different account
		if (accountEmail.equalsIgnoreCase(accountEntity.getEmail()) && existsByEmail(accountEmail)) {
			throw new AccountEmailAlreadyExistsException(accountEmail);
		}

		Integer age = validateAge(accountDto.getAge(), accountEmail);

		String phoneNumber = validatePhoneNumberFormat(accountDto.getPhoneNumber());

		//phone exists or belongs to a different account
		if (phoneNumber.equals(accountEntity.getPhoneNumber()) && existsByPhoneNumber(phoneNumber)) {
			throw new AccountPhoneNumberAlreadyExistsException(phoneNumber);
		}

		// Update entity
		accountEntity.setFirstName(accountDto.getFirstName());
		accountEntity.setLastName(accountDto.getLastName());
		accountEntity.setEmail(accountEmail);
		accountEntity.setAge(age);
		accountEntity.setPhoneNumber(phoneNumber);

		accountRepository.save(accountEntity);
	}

	@Transactional
	public void updateAccountAge(Long id, Integer age) {
		AccountEntity accountEntity = getAccountEntityById(id);
		validateAge(age, accountEntity.getEmail());
		accountEntity.setAge(age);
		accountRepository.save(accountEntity);
	}

	@Transactional
	public void updateAccountEmail(Long id, String email) {
		AccountEntity accountEntity = getAccountEntityById(id);
		String normalizedEmail = validateEmailFormatAndNormalize(email);

		//email exists but belongs to a different account
		if (normalizedEmail.equalsIgnoreCase(accountEntity.getEmail()) && existsByEmail(normalizedEmail)) {
			throw new AccountEmailAlreadyExistsException(normalizedEmail);
		}

		accountEntity.setEmail(normalizedEmail);
		accountRepository.save(accountEntity);
	}

	@Transactional
	public void updateAccountPhoneNumber(Long id, String phoneNumber) {
		AccountEntity accountEntity = getAccountEntityById(id);
		String validatedPhone = validatePhoneNumberFormat(phoneNumber);

		//phone exists but belongs to a different account
		if (validatedPhone.equals(accountEntity.getPhoneNumber()) && existsByPhoneNumber(validatedPhone)) {
			throw new AccountPhoneNumberAlreadyExistsException(validatedPhone);
		}

		accountEntity.setPhoneNumber(validatedPhone);
		accountRepository.save(accountEntity);
	}


	@Transactional
	public void updateAccountRole(Long id, UserRole newRole) {
		AccountEntity account = getAccountEntityById(id);
		account.setUserRole(newRole);
		accountRepository.save(account);
	}

	public AccountDto getAccountDtoById(Long id) {
		return accountMapper.toDto(getAccountEntityById(id));
	}

	public AccountEntity getAccountEntityById(Long id) {
		validateNotNull(id, "id");
		return accountRepository.findById(id)
				.orElseThrow(() -> new AccountNotFoundException(id));
	}

	public List<AccountDto> getDeactivatedAccounts() {
		return accountMapper.toDtoList(accountRepository.findByActiveIsFalse());
	}

	public List<FirstLastNameDto> getFirstNameAndLastNameAccountsThatAreDeactivated() {
		List<AccountEntity> deactivatedAccounts = accountRepository.findByActiveIsFalseOrderByFirstNameAscLastNameAsc();
		return deactivatedAccounts.stream()
				.map(accountMapper::toFirstLastNameDto)
				.toList();
	}

	public List<AccountDto> getAllAccounts(int page, int size) {
		return accountRepository.findAll(PageRequest.of(page, size))
				.map(accountMapper::toDto)
				.toList();
	}

	private boolean existsByEmail(String email) {
		return accountRepository.existsByEmail(email);
	}

	private boolean existsByPhoneNumber(String phoneNumber) {
		return accountRepository.existsByPhoneNumber(phoneNumber);
	}

	// Validation helper methods
	private void validateNotNull(Object obj, String fieldName) {
		if (obj == null) {
			throw new AccountInvalidDataException(fieldName, fieldName + " cannot be null");
		}
	}

	private void validateName(String name, String fieldName) {
		if (name == null || name.trim().isEmpty()) {
			throw new AccountInvalidNameFormatException(fieldName, "Name cannot be empty");
		}

		String trimmedName = name.trim();

		// Length check
		if (trimmedName.length() < 2) {
			throw new AccountInvalidNameFormatException(fieldName,
					String.format("%s is too short (minimum 2 characters)", fieldName));
		}

		if (trimmedName.length() > 50) {
			throw new AccountInvalidNameFormatException(fieldName,
					String.format("%s is too long (maximum 50 characters)", fieldName));
		}

		// Character check
		if (!trimmedName.matches("^[a-zA-Z\\s'-]+$")) {
			throw new AccountInvalidNameFormatException(fieldName,
					String.format("%s can only contain letters, spaces, hyphens and apostrophes", fieldName));
		}
	}

	private String validateEmailFormatAndNormalize(String email) {
		return emailValidator.validateEmailFormatAndNormalize(email);
	}

	private void validateEmailUniqueness(String email) {
		if (existsByEmail(email)) {
			throw new AccountEmailAlreadyExistsException(email);
		}
	}

	private Integer validateAge(Integer age, String email) {
		if (age == null) {
			throw new AccountInvalidDataException("age", "Age cannot be null");
		}

		if (age < 18 || age > 99) {
			throw new AccountInvalidAgeException(email);
		}

		return age;
	}

	private String validatePhoneNumberFormat(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new AccountInvalidDataException("phoneNumber", "Phone number cannot be empty");
		}

		if (!phoneNumber.matches("^(91|92|93|96)\\d{7}$")) {
			throw new AccountInvalidNumberException(phoneNumber);
		}

		return phoneNumber;
	}

	private void validatePhoneNumberUniqueness(String phoneNumber) {
		if (existsByPhoneNumber(phoneNumber)) {
			throw new AccountPhoneNumberAlreadyExistsException(phoneNumber);
		}
	}
}