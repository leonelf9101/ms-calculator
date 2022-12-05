package com.calculator.application.exception.model;

import com.calculator.config.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import lombok.Builder;
import lombok.Value;


/*
Example of response:
{
    "code": "USER_CONFLICT",
    "fields": [                  (OPTIONAL FIELD, if not necessary put empty map)
        "name": "NAME_TOO_LONG",
        "password": "INVALID_PASSWORD"
    ]
}
 */
@Value
@Builder
public class ErrorResponse {
	@JsonIgnore
	int httpStatus;
	ErrorCode code;
	Map<String, String> fields;
}
