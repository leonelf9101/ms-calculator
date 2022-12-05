package com.calculator.adapter.controller;

import com.calculator.application.port.in.GetCalculationQuery;
import com.calculator.adapter.controller.model.CalculatedValueResponse;
import com.calculator.domain.CalculatedValue;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
public class CalculatorController {
    private final GetCalculationQuery getCalculationUseCase;

    private final static String URI = "/calculations";

    public CalculatorController(GetCalculationQuery getCalculationUseCase) {
        this.getCalculationUseCase = getCalculationUseCase;
    }

    @GetMapping(URI)
    @ApiOperation("Get Calculated Value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed correctly"),
            @ApiResponse(code = 400, message = "Malformed request syntax"),
            @ApiResponse(code = 401, message = "Invalid token"),
            @ApiResponse(code = 403, message = "Permission denied"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable"),})
    public CalculatedValueResponse getCalculatedValue(
            @RequestParam(name = "value_1") BigDecimal value1,
            @RequestParam(name = "value_2") BigDecimal value2
    ) {

        GetCalculationQuery.Data data =
                GetCalculationQuery.Data.builder()
                        .value1(value1)
                        .value2(value2)
                        .uri(URI + "?value_1=" + value1 + "&value_2=" + value2)
                        .build();

        CalculatedValue calculatedValue = getCalculationUseCase.execute(data);

        return CalculatedValueResponse.builder()
                .result(calculatedValue.getResult())
                .build();
    }
}
