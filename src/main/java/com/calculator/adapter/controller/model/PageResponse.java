package com.calculator.adapter.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.calculator.domain.Page;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageResponse<T> {
    List<T> content;
    Integer totalElements;
    Integer size;
    Integer number;
    Integer totalPages;

    public <U> PageResponse<T> fromDomain(Page<U> domain, Function<U, T> convert) {
        return PageResponse.<T>builder()
            .content(domain.getContent()
                .stream()
                .map(convert)
                .collect(Collectors.toList()))
            .totalElements(domain.getTotalElements())
            .size(domain.getSize())
            .number(domain.getNumber())
            .totalPages(domain.getTotalPages())
            .build();
    }
}
