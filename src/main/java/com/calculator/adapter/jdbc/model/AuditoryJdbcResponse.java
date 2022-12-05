package com.calculator.adapter.jdbc.model;

import com.calculator.domain.Auditory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditoryJdbcResponse {
    Integer id;
    String uri;
    String responseBody;
    Integer responseStatus;
    Instant createdAt;

    Long elapsedTime;

    public Auditory toDomain(){
        return Auditory.builder()
                .id(id)
                .uri(uri)
                .responseBody(responseBody)
                .responseStatus(responseStatus)
                .createdAt(createdAt)
                .elapsedTime(elapsedTime)
                .build();
    }
}
