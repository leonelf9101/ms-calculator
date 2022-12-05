package com.calculator.application.exception;

import com.calculator.config.ErrorCode;
import org.springframework.http.HttpStatus;

public class BusinessException extends HttpErrorException {

    public BusinessException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST.value(), errorCode);
    }
}
