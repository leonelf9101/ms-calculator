package com.calculator.application.usecase;

import com.calculator.application.exception.EntityBadRequestException;
import com.calculator.application.exception.RepositoryNotAvailableException;
import com.calculator.application.port.in.GetCalculationQuery;
import com.calculator.application.port.out.PercentageAuditRepository;
import com.calculator.application.port.out.PercentageRepository;
import com.calculator.config.ErrorCode;
import com.calculator.domain.CalculatedValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class GetCalculationUseCase implements GetCalculationQuery {


    private final CacheManager cacheManager;
    private final PercentageRepository percentageRestAdapter;

    private final PercentageAuditRepository percentageAuditJdbcAdapter;

    public GetCalculationUseCase(
            PercentageAuditRepository percentageAuditJdbcAdapter,
            CacheManager cacheManager,
            PercentageRepository percentageRestAdapter
    ) {
        this.percentageAuditJdbcAdapter = percentageAuditJdbcAdapter;
        this.cacheManager = cacheManager;
        this.percentageRestAdapter = percentageRestAdapter;
    }

    @Override
    public CalculatedValue execute(Data data) {

        validateData(data);

        BigDecimal percentage = getPercentage();

        Long start = System.currentTimeMillis();

        BigDecimal result = calculateResult(percentage, data.getValue1(), data.getValue2());

        Long finish = System.currentTimeMillis();

        Long timeElapsed = finish - start;

        CalculatedValue calculatedValue = CalculatedValue.builder()
                .result(result)
                .build();

        percentageAuditJdbcAdapter.save(data.getUri(), calculatedValue, 200, timeElapsed);

        return calculatedValue;
    }

    private void validateData(Data data) {
        if(data.getValue1() == null || data.getValue2() == null)
            throw new EntityBadRequestException(ErrorCode.INVALID_PARAMETER);
    }

    private BigDecimal calculateResult(BigDecimal percentage, BigDecimal value1, BigDecimal value2) {
        BigDecimal coefficient = percentage.divide(BigDecimal.valueOf(100));

        BigDecimal percentageCalculated = value1.add(value2).multiply(coefficient);

        BigDecimal result = value1.add(value2).add(percentageCalculated);

        log.info("Calculated value = value_1 + value_v2 + ( value_1 + value_2) * (percentage/100)");
        log.info("{} = {} + {} + ({} + {}) * ({}/100)", result, value1, value2, value1, value2, percentage);

        return result;
    }

    private BigDecimal getPercentage() {

        try {
            return percentageRestAdapter.getPercentage();
        } catch (Exception ex) {
            log.error("Error when try to get percetange from repository.");
            try {
                BigDecimal lastPercentageRetuned = cacheManager.getCache("lastPercentageReturned").get("lastPercentageReturned", BigDecimal.class);
                log.info("Last percentage returned : {}", lastPercentageRetuned);

                if(lastPercentageRetuned == null)
                    throw new RepositoryNotAvailableException(ErrorCode.PERCENTAGE_NOT_AVAILABLE);

                return lastPercentageRetuned;
            } catch (Exception ex2){
                throw new RepositoryNotAvailableException(ErrorCode.PERCENTAGE_NOT_AVAILABLE);
            }
        }
    }
}
