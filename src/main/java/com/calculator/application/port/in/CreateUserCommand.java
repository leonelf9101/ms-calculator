package com.calculator.application.port.in;

import com.calculator.domain.User;
import lombok.Builder;
import lombok.Value;

public interface CreateUserCommand {

    User execute(Data data);

    @Value
    @Builder
    class Data {
        String username;
        String password;
    }
}

