package com.calculator.application.usecase;

import com.calculator.application.port.in.GetAuditoryListQuery;
import com.calculator.application.port.out.PercentageAuditRepository;
import com.calculator.domain.Auditory;
import com.calculator.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GetAuditoryListUseCase implements GetAuditoryListQuery {

    private final PercentageAuditRepository percentageAuditJdbcAdapter;

    public GetAuditoryListUseCase(PercentageAuditRepository percentageAuditJdbcAdapter) {
        this.percentageAuditJdbcAdapter = percentageAuditJdbcAdapter;
    }

    @Override
    public Page<Auditory> execute(Data data) {
        return percentageAuditJdbcAdapter.findAll(data.getPage(), data.getSize());
    }
}
