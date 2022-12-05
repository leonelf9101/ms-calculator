package com.calculator.application.port.out;

import com.calculator.domain.Auditory;
import com.calculator.domain.Page;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface PercentageAuditRepository {
    void save(String uri, Object responseBody, Integer responseHttpStatus, Long elapsedTime);

    Page<Auditory> findAll(Integer page, Integer size);
}
