package com.calculator.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CalculatedValue {
    BigDecimal result;
}
