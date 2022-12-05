package com.calculator.adapter.controller

import com.calculator.application.exception.EntityBadRequestException
import com.calculator.application.exception.EntityConflictException
import com.calculator.application.exception.EntityNotFoundException
import com.calculator.application.exception.RepositoryNotAvailableException
import com.calculator.application.port.in.GetAuditoryListQuery
import com.calculator.application.port.in.GetCalculationQuery
import com.calculator.config.ErrorCode
import com.calculator.config.ErrorHandler
import com.calculator.domain.Auditory
import com.calculator.domain.Page
import org.hamcrest.core.IsNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.Instant

import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class AuditoryControllerTest extends Specification {
    GetAuditoryListQuery getAuditoryListQuery = Mock(GetAuditoryListQuery)
    AuditoryController target = new AuditoryController(getAuditoryListQuery)

    MockMvc mvc = standaloneSetup(target).setControllerAdvice(new ErrorHandler()).build()

    def "given an invocation to GET /calculations/audits without pagination, when the response from the get auditory query is ok then return all the auditory registries"() {
        given:
        def data = GetAuditoryListQuery.Data.builder().build()

        def calculationsAudit = List.of(
                Auditory.builder()
                        .id(1)
                        .uri("/calculations?value_1=5&value_2=10")
                        .responseBody("""{ "result": 16.5 }""")
                        .responseStatus(200)
                        .createdAt(Instant.now())
                        .elapsedTime(240000)
                        .build(),
                Auditory.builder()
                        .id(2)
                        .uri("/calculations?value_1=7&value_2=20")
                        .responseBody("""{ "result": 32.4 }""")
                        .responseStatus(200)
                        .createdAt(Instant.now())
                        .elapsedTime(250000)
                        .build(),
                Auditory.builder()
                        .id(3)
                        .uri("/calculations?value_1=9&value_2=10")
                        .responseBody("""{ "result": 20.9 }""")
                        .responseStatus(200)
                        .createdAt(Instant.now())
                        .elapsedTime(140000)
                        .build()
        )

        def calculationsPage = Page.<Auditory>builder()
                .content(calculationsAudit)
                .totalElements(3)
                .totalPages(0)
                .build()

        when:
        def result = mvc.perform(get("/calculations/audits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.content.[0].id', is(1)))
                .andExpect(jsonPath('$.content.[0].uri', is("/calculations?value_1=5&value_2=10")))
                .andExpect(jsonPath('$.content.[0].response_body', is("""{ "result": 16.5 }""")))
                .andExpect(jsonPath('$.content.[0].response_status', is(200)))
                .andExpect(jsonPath('$.content.[0].created_at', is(IsNull.notNullValue())))
                .andExpect(jsonPath('$.content.[0].elapsed_time', is(240000)))
                .andExpect(jsonPath('$.content.[1].id', is(2)))
                .andExpect(jsonPath('$.content.[1].uri', is("/calculations?value_1=7&value_2=20")))
                .andExpect(jsonPath('$.content.[1].response_body', is("""{ "result": 32.4 }""")))
                .andExpect(jsonPath('$.content.[1].response_status', is(200)))
                .andExpect(jsonPath('$.content.[1].created_at', is(IsNull.notNullValue())))
                .andExpect(jsonPath('$.content.[1].elapsed_time', is(250000)))
                .andExpect(jsonPath('$.content.[2].id', is(3)))
                .andExpect(jsonPath('$.content.[2].uri', is("/calculations?value_1=9&value_2=10")))
                .andExpect(jsonPath('$.content.[2].response_body', is("""{ "result": 20.9 }""")))
                .andExpect(jsonPath('$.content.[2].response_status', is(200)))
                .andExpect(jsonPath('$.content.[2].created_at', is(IsNull.notNullValue())))
                .andExpect(jsonPath('$.content.[2].elapsed_time', is(140000)))
                .andReturn()

        then:
        1 * getAuditoryListQuery.execute(data) >> calculationsPage
        result.response.status == 200
    }

    def "given an invocation to GET /calculations/audits with pagination, when the response from the get auditory query is ok then return all the auditory registries"() {
        given:
        def data = GetAuditoryListQuery.Data.builder().size(2).page(0).build()

        def calculationsAudit = List.of(
                Auditory.builder()
                        .id(1)
                        .uri("/calculations?value_1=5&value_2=10")
                        .responseBody("""{ "result": 16.5 }""")
                        .responseStatus(200)
                        .createdAt(Instant.now())
                        .elapsedTime(240000)
                        .build(),
                Auditory.builder()
                        .id(2)
                        .uri("/calculations?value_1=7&value_2=20")
                        .responseBody("""{ "result": 32.4 }""")
                        .responseStatus(200)
                        .createdAt(Instant.now())
                        .elapsedTime(250000)
                        .build()
        )

        def calculationsPage = Page.<Auditory>builder()
                .content(calculationsAudit)
                .size(2)
                .number(0)
                .totalElements(3)
                .totalPages(2)
                .build()

        when:
        def result = mvc.perform(get("/calculations/audits?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.content.[0].id', is(1)))
                .andExpect(jsonPath('$.content.[0].uri', is("/calculations?value_1=5&value_2=10")))
                .andExpect(jsonPath('$.content.[0].response_body', is("""{ "result": 16.5 }""")))
                .andExpect(jsonPath('$.content.[0].response_status', is(200)))
                .andExpect(jsonPath('$.content.[0].created_at', is(IsNull.notNullValue())))
                .andExpect(jsonPath('$.content.[0].elapsed_time', is(240000)))
                .andExpect(jsonPath('$.content.[1].id', is(2)))
                .andExpect(jsonPath('$.content.[1].uri', is("/calculations?value_1=7&value_2=20")))
                .andExpect(jsonPath('$.content.[1].response_body', is("""{ "result": 32.4 }""")))
                .andExpect(jsonPath('$.content.[1].response_status', is(200)))
                .andExpect(jsonPath('$.content.[1].created_at', is(IsNull.notNullValue())))
                .andExpect(jsonPath('$.content.[1].elapsed_time', is(250000)))
                .andReturn()

        then:
        1 * getAuditoryListQuery.execute(data) >> calculationsPage
        result.response.status == 200
    }

    def "given an invocation to GET /calculations/audits when the get auditor query throws {exception} then {status} is answered with the expected message"() {
        given:
        getAuditoryListQuery.execute(_ as GetAuditoryListQuery.Data) >> { throw exception }

        when:
        def result = mvc.perform(get("/calculations/audits")
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
