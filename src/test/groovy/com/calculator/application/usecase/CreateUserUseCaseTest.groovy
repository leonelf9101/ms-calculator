package com.calculator.application.usecase

import com.calculator.application.exception.EntityBadRequestException
import com.calculator.application.port.in.CreateUserCommand
import com.calculator.application.port.out.UserRepository
import com.calculator.domain.User
import spock.lang.Specification

class CreateUserUseCaseTest extends Specification {

    UserRepository userJdbcAdapter = Mock(UserRepository)
    CreateUserUseCase target = new CreateUserUseCase(userJdbcAdapter)

    def "given a valid username and password when execute usecase then should return an user with id"() {
        given:
            def username = "admin"
            def password = "12345"
            CreateUserCommand.Data data =
                    CreateUserCommand.Data.builder()
                            .username(username)
                            .password(password)
                            .build()

            User user = User.builder()
                    .username(username)
                    .password(password)
                    .enabled(true)
                    .build()

            def expected = user.withId(123)

        when:
            def result = target.execute(data)

        then:
            1 * userJdbcAdapter.findByUsername(data.getUsername()) >> null
            1 * userJdbcAdapter.save(user) >> user.withId(123)

            result == expected
    }

    def "given a valid username that already exist when execute usecase then should return a EntityBadRequestException"() {
        given:
            def username = "admin"
            def password = "12345"
            CreateUserCommand.Data data =
                    CreateUserCommand.Data.builder()
                            .username(username)
                            .password(password)
                            .build()

            User user = User.builder()
                    .username(username)
                    .password(password)
                    .enabled(true)
                    .build()

        when:
            target.execute(data)

        then:
            1 * userJdbcAdapter.findByUsername(data.getUsername()) >> user
            0 * userJdbcAdapter.save(_)

            thrown(EntityBadRequestException)
    }
}
