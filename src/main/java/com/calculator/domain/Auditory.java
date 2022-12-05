package com.calculator.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Value
@Builder
public class Auditory {
    Integer id;
    String uri;
    String responseBody;
    Integer responseStatus;
    Instant createdAt;
    Long elapsedTime;
}
