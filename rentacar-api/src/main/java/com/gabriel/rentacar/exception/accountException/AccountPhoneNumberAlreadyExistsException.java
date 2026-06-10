package com.gabriel.rentacar.exception.accountException;

import com.gabriel.rentacar.exception.ValidationException;

import java.io.Serial;

public class AccountPhoneNumberAlreadyExistsException extends ValidationException {
  @Serial
  private static final long serialVersionUID = 1L;

  public AccountPhoneNumberAlreadyExistsException(String phoneNumber) {
    super(String.format("Phone number already exists: %s", phoneNumber),
            "This phone number is already registered");
  }
}