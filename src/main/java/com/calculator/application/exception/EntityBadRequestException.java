package com.calculator.application.exception;

import com.calculator.config.ErrorCode;
import org.springframework.http.HttpStatus;

public class EntityBadRequestException extends HttpErrorException {

    public EntityBadRequestException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST.value(), errorCode);
    }
}
