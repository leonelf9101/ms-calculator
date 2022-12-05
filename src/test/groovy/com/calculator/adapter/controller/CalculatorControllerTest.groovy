package com.calculator.adapter.controller

import com.calculator.application.exception.EntityBadRequestException
import com.calculator.application.exception.EntityConflictException
import com.calculator.application.exception.EntityNotFoundException
import com.calculator.application.exception.RepositoryNotAvailableException
import com.calculator.application.port.in.GetCalculationQuery
import com.calculator.config.ErrorCode
import com.calculator.config.ErrorHandler
import com.calculator.domain.CalculatedValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class CalculatorControllerTest extends Specification {

    GetCalculationQuery getCalculationQuery = Mock(GetCalculationQuery)
    CalculatorController target = new CalculatorController(getCalculationQuery)

    MockMvc mvc = standaloneSetup(target).setControllerAdvice(new ErrorHandler()).build()

    def "given an invocation to GET /calculations with 2 values and a 10% increase, when the response from the get calculation query is ok then return the result"() {
        given:
        def value1 = 5
        def value2 = 5
        def uri = "/calculations?value_1=5&value_2=5"

        def data = GetCalculationQuery.Data.builder()
                .uri(uri)
                .value1(BigDecimal.valueOf(value1))
                .value2(BigDecimal.valueOf(value2))
                .build()

        when:
        def result = mvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.result', is(11)))
                .andReturn()

        then:
        1 * getCalculationQuery.execute(data) >> CalculatedValue.builder().result(BigDecimal.valueOf(11)).build()
        result.response.status == 200
    }

    def "given an invocation to GET /calculations when the calculation query throws {exception} then {status} is answered with the expected message"() {
        given:
        getCalculationQuery.execute(_ as GetCalculationQuery.Data) >> { throw exception }

        when:
        def result = mvc.perform(get("/calculations?value_1=5&value_2=5")
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

}
