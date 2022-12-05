package com.calculator.adapter.controller

import com.calculator.adapter.controller.model.UserRequest
import com.calculator.application.exception.EntityBadRequestException
import com.calculator.application.exception.EntityConflictException
import com.calculator.application.exception.EntityNotFoundException
import com.calculator.application.exception.RepositoryNotAvailableException
import com.calculator.application.port.in.CreateUserCommand
import com.calculator.config.ErrorCode
import com.calculator.config.ErrorHandler
import com.calculator.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UserControllerTest extends Specification {
    CreateUserCommand createUserCommand = Mock(CreateUserCommand)
    UserController target = new UserController(createUserCommand)

    ObjectMapper objectMapper = new ObjectMapper()

    MockMvc mvc = standaloneSetup(target).setControllerAdvice(new ErrorHandler()).build()

    def "given an invocation to POST /signup when the create user command response is ok then return the user created"() {
        given:
        def request = UserRequest.builder().username("batman").password("batman123").build()
        def data = CreateUserCommand.Data.builder().username("batman").password("batman123").build()

        def createdUser = User.builder()
                .id(1)
                .username("batman")
                .password("batman123")
                .authority("ROLE_ADMIN")
                .enabled(true)
                .build()

        when:
        def result = mvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath('$.id', is(1)))
                .andExpect(jsonPath('$.username', is("batman")))
                .andReturn()

        then:
        1 * createUserCommand.execute(data) >> createdUser
        result.response.status == 200
    }

    def "given an invocation to POST /signup when the create user command throws {exception} then {status} is answered with the expected message"() {
        given:
        def request = UserRequest.builder().username("robin").password("robin123").build()
        createUserCommand.execute(_ as CreateUserCommand.Data) >> { throw exception }

        when:
        def result = mvc.perform(post("/signup")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .response

        then:
        result.status == status
        result.getContentAsString().contains(expectedBody)

        where:
        status | exception                                                               | expectedBody
        400    | new EntityBadRequestException(ErrorCode.REPOSITORY_BAD_REQUEST)         | ErrorCode.REPOSITORY_BAD_REQUEST.name()
        404    | new EntityNotFoundException(ErrorCode.REPOSITORY_NOT_FOUND)             | ErrorCode.REPOSITORY_NOT_FOUND.name()
        409    | new EntityConflictException(ErrorCode.REPOSITORY_CONFLICT)              | ErrorCode.REPOSITORY_CONFLICT.name()
        503    | new RepositoryNotAvailableException(ErrorCode.REPOSITORY_NOT_AVAILABLE) | ErrorCode.REPOSITORY_NOT_AVAILABLE.name()
        503    | new RepositoryNotAvailableException(ErrorCode.PERCENTAGE_NOT_AVAILABLE) | ErrorCode.PERCENTAGE_NOT_AVAILABLE.name()
        500    | new RuntimeException("Unexpected error")                                | ""
    }

    def "given an invocation to POST /signup with an invalid body then throw exception"() {
        given:

        when:
        def result = mvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request_body)))
                .andReturn()

        then:
        result.response.status == http_status

        where:
        http_status      | request_body
        400              | UserRequest.builder().username("Pedro").build()
        400              | UserRequest.builder().password("Pedro123").build()
        400              | UserRequest.builder().build()
        400              | null
    }
}
