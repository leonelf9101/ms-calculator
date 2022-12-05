package com.calculator.application.usecase;

import com.calculator.application.exception.EntityBadRequestException;
import com.calculator.application.port.in.CreateUserCommand;
import com.calculator.application.port.out.UserRepository;
import com.calculator.config.ErrorCode;
import com.calculator.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateUserUseCase implements CreateUserCommand {

    private final UserRepository userJdbcAdapter;


    public CreateUserUseCase(
            UserRepository userJdbcAdapter
    ) {
        this.userJdbcAdapter = userJdbcAdapter;
    }

    @Override
    public User execute(Data data) {
        if(userJdbcAdapter.findByUsername(data.getUsername()) != null)
            throw new EntityBadRequestException(ErrorCode.USER_ALREADY_EXIST);

        User user = User.builder()
                .username(data.getUsername())
                .password(data.getPassword())
                .enabled(true)
                .build();

        return userJdbcAdapter.save(user);
    }

}
