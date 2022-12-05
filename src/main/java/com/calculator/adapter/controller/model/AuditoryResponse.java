package com.calculator.adapter.controller.model;

import com.calculator.domain.Auditory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Date;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class AuditoryResponse {
    Integer id;
    String uri;
    String responseBody;
    Integer responseStatus;
    Date createdAt;
    Long elapsedTime;

    public static AuditoryResponse fromDomain(Auditory auditory){
        return AuditoryResponse.builder()
                .id(auditory.getId())
                .uri(auditory.getUri())
                .responseBody(auditory.getResponseBody())
                .responseStatus(auditory.getResponseStatus())
                .createdAt(auditory.getCreatedAt() != null? Date.from(auditory.getCreatedAt()): null)
                .elapsedTime(auditory.getElapsedTime())
                .build();
    }
}
