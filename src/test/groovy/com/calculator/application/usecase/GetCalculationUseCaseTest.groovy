package com.calculator.application.usecase

import com.calculator.application.exception.EntityBadRequestException
import com.calculator.application.exception.RepositoryNotAvailableException
import com.calculator.application.port.in.GetCalculationQuery
import com.calculator.application.port.out.PercentageAuditRepository
import com.calculator.application.port.out.PercentageRepository
import com.calculator.domain.CalculatedValue
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class GetCalculationUseCaseTest extends Specification {

    PercentageAuditRepository percentageAuditJdbcAdapter = Mock()
    PercentageRepository percentageRestAdapter = Mock()
    CacheManager cacheManager = Mock()
    GetCalculationUseCase target = new GetCalculationUseCase(percentageAuditJdbcAdapter, cacheManager, percentageRestAdapter)

    def "given a valid data when execute usecase should return calculated value"() {
        given:
            GetCalculationQuery.Data data = GetCalculationQuery.Data.builder()
                    .uri("/calculations?value_1=5&value_2=5")
                    .value1(5)
                    .value2(5)
                    .build()
            def expected = CalculatedValue.builder().result(11.0).build()

        when:
            def result = target.execute(data)

        then:
            1 * percentageRestAdapter.getPercentage() >> 10
            1 * percentageAuditJdbcAdapter.save(data.getUri(), expected, 200, _)
            result == expected
    }

    def "given a valid data when endpoint get percentege isn't working should return last parcentage returned"() {
        given:
            GetCalculationQuery.Data data = GetCalculationQuery.Data.builder()
                    .uri("/calculations?value_1=5&value_2=5")
                    .value1(5)
                    .value2(5)
                    .build()

            ConcurrentMap<Object, Object> map =
                    new ConcurrentHashMap(Map.of("lastPercentageReturned", BigDecimal.valueOf(10)))

            def expected = CalculatedValue.builder().result(11.0).build()

        when:
            def result = target.execute(data)

        then:
            1 * percentageRestAdapter.getPercentage() >> { throw new RuntimeException() }
            1 * cacheManager.getCache("lastPercentageReturned") >> new ConcurrentMapCache("lastPercentageReturned", map, true)
            1 * percentageAuditJdbcAdapter.save(data.getUri(), expected, 200, _)
            result == expected
    }

    def "given a valid data when don't have last percentage returned should return an exception"() {
        given:
            GetCalculationQuery.Data data = GetCalculationQuery.Data.builder()
                    .uri("/calculations?value_1=5&value_2=5")
                    .value1(5)
                    .value2(5)
                    .build()

        when:
            target.execute(data)

        then:
            1 * percentageRestAdapter.getPercentage() >> { throw new RuntimeException() }
            1 * cacheManager.getCache("lastPercentageReturned") >> null
            0 * percentageAuditJdbcAdapter.save(_, _, _, _)

            thrown(RepositoryNotAvailableException)
    }

    def "given a valid data when call to cache isn't working should return an exception"() {
        given:
            GetCalculationQuery.Data data = GetCalculationQuery.Data.builder()
                    .uri("/calculations?value_1=5&value_2=5")
                    .value1(5)
                    .value2(5)
                    .build()

        when:
            target.execute(data)

        then:
            1 * percentageRestAdapter.getPercentage() >> { throw new RuntimeException() }
            1 * cacheManager.getCache("lastPercentageReturned") >> { throw new RuntimeException() }
            0 * percentageAuditJdbcAdapter.save(_, _, _, _)
            thrown(RepositoryNotAvailableException)
    }

    def "given a null data when execute usecase should return a bad_request exception"() {
        given:
            GetCalculationQuery.Data data = GetCalculationQuery.Data.builder()
                    .uri("/calculations")
                    .value1(null)
                    .value2(null)
                    .build()

        when:
            target.execute(data)

        then:
            0 * percentageAuditJdbcAdapter.save(_, _, _, _)
            thrown(EntityBadRequestException)
    }

}
