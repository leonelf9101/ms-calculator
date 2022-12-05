package com.calculator.application.exception;

import com.calculator.config.ErrorCode;
import org.springframework.http.HttpStatus;

public class RepositoryNotAvailableException extends HttpErrorException {

    public RepositoryNotAvailableException(ErrorCode errorCode) {
        super(HttpStatus.SERVICE_UNAVAILABLE.value(), errorCode);
    }
}
