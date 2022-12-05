package com.calculator.application.exception;

import com.calculator.config.ErrorCode;
import org.springframework.http.HttpStatus;

public class EntityConflictException extends HttpErrorException {

    public EntityConflictException(ErrorCode errorCode) {

        super(HttpStatus.CONFLICT.value(), errorCode);
    }
}
