package com.vendor.management.system.user.service.application.exception;

import com.vendor.management.system.application.handler.ErrorDTO;
import com.vendor.management.system.application.handler.GlobalExceptionHandler;
import com.vendor.management.system.domain.exception.DomainException;
import com.vendor.management.system.user.service.domain.exception.UserDataAccessException;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;
import com.vendor.management.system.user.service.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class UserGlobalExceptionHandler extends GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = {UserDataAccessException.class, UserDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(DomainException domainException) {
        log.error(domainException.getMessage(), domainException);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(domainException.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(UserNotFoundException notFoundException) {
        log.error(notFoundException.getMessage(), notFoundException);
        return ErrorDTO.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(notFoundException.getMessage())
                .build();
    }
}
