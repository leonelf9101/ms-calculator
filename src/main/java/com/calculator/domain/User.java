package com.calculator.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;

@Value
@Builder
public class User {
    @With
    Integer id;
    String username;
    String password;
    String authority;
    Boolean enabled;
}
