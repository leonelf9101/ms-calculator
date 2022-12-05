package com.calculator.config;

public enum ErrorCode {

    REPOSITORY_BAD_REQUEST(Constants.REPOSITORY_BAD_REQUEST),
    REPOSITORY_NOT_FOUND(Constants.REPOSITORY_NOT_FOUND),
    REPOSITORY_CONFLICT(Constants.REPOSITORY_CONFLICT),
    REPOSITORY_NOT_AVAILABLE(Constants.REPOSITORY_NOT_AVAILABLE),
    UNEXPECTED_ERROR(Constants.UNEXPECTED_ERROR),
    INVALID_PARAMETER(Constants.INVALID_PARAMETER),
    PERCENTAGE_NOT_AVAILABLE(Constants.PERCENTAGE_NOT_AVAILABLE),
    USER_ALREADY_EXIST(Constants.USER_ALREADY_EXIST);

    ErrorCode(String value) {
    }

    public static class Constants {
        public static final String REPOSITORY_BAD_REQUEST = "REPOSITORY_BAD_REQUEST";
        public static final String REPOSITORY_NOT_FOUND = "REPOSITORY_NOT_FOUND";
        public static final String REPOSITORY_CONFLICT = "REPOSITORY_CONFLICT";
        public static final String REPOSITORY_NOT_AVAILABLE = "REPOSITORY_NOT_AVAILABLE";
        public static final String INVALID_PARAMETER = "INVALID_PARAMETER";
        public static final String PERCENTAGE_NOT_AVAILABLE = "PERCENTAGE_NOT_AVAILABLE";
        public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
        public static final String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";

    }
}
