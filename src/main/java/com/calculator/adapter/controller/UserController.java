package com.calculator.adapter.controller;

import com.calculator.adapter.controller.model.UserRequest;
import com.calculator.adapter.controller.model.UserResponse;
import com.calculator.application.port.in.CreateUserCommand;
import com.calculator.domain.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class UserController {
    private final CreateUserCommand createUserUseCase;

    private final static String URI = "/signup";

    public UserController(CreateUserCommand createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping(URI)
    @ApiOperation("Create user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed correctly"),
            @ApiResponse(code = 400, message = "Malformed request syntax"),
            @ApiResponse(code = 401, message = "Invalid token"),
            @ApiResponse(code = 403, message = "Permission denied"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable"),})
    public UserResponse createUser(
            @RequestBody @Valid UserRequest body
    ) {

        CreateUserCommand.Data data =
                CreateUserCommand.Data.builder()
                        .username(body.getUsername())
                        .password(body.getPassword())
                        .build();

        User user = createUserUseCase.execute(data);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
