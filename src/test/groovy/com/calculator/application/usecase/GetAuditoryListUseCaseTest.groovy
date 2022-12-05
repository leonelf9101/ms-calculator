package com.calculator.application.usecase

import com.calculator.application.exception.EntityNotFoundException
import com.calculator.application.port.in.GetAuditoryListQuery
import com.calculator.application.port.out.PercentageAuditRepository
import com.calculator.config.ErrorCode
import com.calculator.domain.Auditory
import com.calculator.domain.Page
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class GetAuditoryListUseCaseTest extends Specification {

    PercentageAuditRepository percentageAuditJdbcAdapter = Mock()

    GetAuditoryListUseCase target = new GetAuditoryListUseCase(percentageAuditJdbcAdapter)

    def "given a valid data when execute usecase should return the auditory list returned by the repository"() {
        given:
            GetAuditoryListQuery.Data data =
                    GetAuditoryListQuery.Data.builder()
                            .page(1)
                            .size(1)
                            .build()

            LocalDate date = LocalDate.parse("2022-12-04");
            Instant createdAt = date.atStartOfDay(ZoneId.of("Europe/Paris")).toInstant();

            def expected = Page<Auditory>.builder()
                    .content(List.of(
                            Auditory.builder()
                                    .uri("/calculations?value_1=5&value_2=5")
                                    .responseBody("{\"result\":11}")
                                    .elapsedTime(2)
                                    .createdAt(createdAt)
                                    .build()
                    ))
                    .build()

        when:
            def result = target.execute(data)

        then:
            1 * percentageAuditJdbcAdapter.findAll(1, 1) >> expected
            result == expected
    }

    def "given a valid data when repository isn't working should return an exception"() {
        given:
            GetAuditoryListQuery.Data data =
                    GetAuditoryListQuery.Data.builder()
                            .page(1)
                            .size(1)
                            .build()

        when:
            target.execute(data)

        then:
            1 * percentageAuditJdbcAdapter.findAll(1, 1) >> { throw new EntityNotFoundException(ErrorCode.REPOSITORY_NOT_FOUND) }
            thrown(EntityNotFoundException)
    }
}
