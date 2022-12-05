package com.calculator.config;

import com.calculator.application.exception.BusinessException;
import com.calculator.application.exception.EntityBadRequestException;
import com.calculator.application.exception.EntityConflictException;
import com.calculator.application.exception.EntityNotFoundException;
import com.calculator.application.exception.RepositoryNotAvailableException;
import com.calculator.application.exception.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {


    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handle(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_PARAMETER)
                .fields(Map.of(ex.getParameterName(),"required_field"))
                .build();
        log.error("Error handled : ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({EntityBadRequestException.class})
    public ResponseEntity<ErrorResponse> handle(EntityBadRequestException ex) {
        log.error("Error handled : ", ex);
        return ResponseEntity.status(ex.getErrorResponse().getHttpStatus()).body(ex.getErrorResponse());
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ErrorResponse> handle(BusinessException ex) {
        log.error("Error handled : ", ex);
        return ResponseEntity.status(ex.getErrorResponse().getHttpStatus()).body(ex.getErrorResponse());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException ex) {
        log.error("Error handled : ", ex);
        return ResponseEntity.status(ex.getErrorResponse().getHttpStatus()).body(ex.getErrorResponse());
    }

    @ExceptionHandler({EntityConflictException.class})
    public ResponseEntity<ErrorResponse> handle(EntityConflictException ex) {
        log.error("Error handled : ", ex);
        return ResponseEntity.status(ex.getErrorResponse().getHttpStatus()).body(ex.getErrorResponse());
    }

    @ExceptionHandler({RepositoryNotAvailableException.class})
    public ResponseEntity<ErrorResponse> handle(RepositoryNotAvailableException ex) {
        log.error("Error handled : ", ex);
        return ResponseEntity.status(ex.getErrorResponse().getHttpStatus()).body(ex.getErrorResponse());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException ex) {
        log.error("Bad argument", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
        log.error("Bad argument", ex);
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(camelToSnake(fieldName), errorMessage);
        });
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.REPOSITORY_BAD_REQUEST)
                .fields(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException ex) {
        log.error("Unexpected Error", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.REPOSITORY_BAD_REQUEST)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ErrorResponse> handle(Throwable ex) {
        log.error("Unexpected Error", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.UNEXPECTED_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private static String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}

