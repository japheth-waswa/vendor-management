package com.vendor.management.system.stock.service.application.exception.handler;

import com.vendor.management.system.stock.service.domain.exception.OrderNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductCategoryNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.application.handler.ErrorDTO;
import com.vendor.management.system.application.handler.GlobalExceptionHandler;
import com.vendor.management.system.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class StockGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {StockDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(StockDomainException stockDomainException) {
        log.error(stockDomainException.getMessage(), stockDomainException);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(stockDomainException.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {ProductCategoryNotFoundException.class,
            ProductNotFoundException.class,
            OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(DomainException notFoundException) {
        log.error(notFoundException.getMessage(), notFoundException);
        return ErrorDTO.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(notFoundException.getMessage())
                .build();
    }
}
