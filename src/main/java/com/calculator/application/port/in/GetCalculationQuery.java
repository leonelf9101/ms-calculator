package com.calculator.application.port.in;

import com.calculator.domain.CalculatedValue;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

public interface GetCalculationQuery {

    CalculatedValue execute(Data data);

    @Value
    @Builder
    class Data {
        String uri;
        BigDecimal value1;
        BigDecimal value2;
    }
}

