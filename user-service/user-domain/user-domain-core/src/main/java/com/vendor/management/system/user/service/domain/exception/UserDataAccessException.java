package com.vendor.management.system.user.service.domain.exception;

import com.vendor.management.system.domain.exception.DomainException;

public class UserDataAccessException extends DomainException {
    public UserDataAccessException(String message) {
        super(message);
    }

    public UserDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
