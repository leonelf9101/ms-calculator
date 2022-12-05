package com.calculator.application.exception;

import com.calculator.config.ErrorCode;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends HttpErrorException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(HttpStatus.NOT_FOUND.value(), errorCode);
    }
}
