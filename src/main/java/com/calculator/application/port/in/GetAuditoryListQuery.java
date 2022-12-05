package com.calculator.application.port.in;

import com.calculator.domain.Auditory;
import com.calculator.domain.Page;
import lombok.Builder;
import lombok.Value;

import java.util.List;

public interface GetAuditoryListQuery {

    Page<Auditory> execute(Data data);

    @Value
    @Builder
    class Data {
        Integer page;
        Integer size;
    }
}

