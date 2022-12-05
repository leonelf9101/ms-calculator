package com.calculator.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Page<T> {
    List<T> content;
    Integer totalElements;
    Integer size;
    Integer number;
    Integer totalPages;
}
