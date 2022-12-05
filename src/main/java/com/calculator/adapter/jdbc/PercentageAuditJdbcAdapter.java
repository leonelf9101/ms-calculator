package com.calculator.adapter.jdbc;

import com.calculator.adapter.jdbc.model.AuditoryJdbcResponse;
import com.calculator.application.exception.EntityConflictException;
import com.calculator.application.exception.EntityNotFoundException;
import com.calculator.application.exception.RepositoryNotAvailableException;
import com.calculator.application.port.out.PercentageAuditRepository;
import com.calculator.config.ErrorCode;
import com.calculator.domain.Auditory;
import com.calculator.domain.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;

@Component
@Slf4j
public class PercentageAuditJdbcAdapter implements PercentageAuditRepository {

    private static final String PATH_INSERT_PERCENTAGE_AUDIT = "sql/insertPercentageAudit.sql";
    private static final String PATH_GET_PERCENTAGE_AUDIT_LIST = "sql/getPercentageAuditList.sql";

    private final String insertPercentageAuditCommand;
    private final String getPercentageAuditListQuery;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String RESPONSE_BODY = "RESPONSE_BODY";
    private static final String URI = "URI";
    private static final String RESPONSE_STATUS = "RESPONSE_STATUS";

    private static final String ELAPSED_TIME = "ELAPSED_TIME";

    public PercentageAuditJdbcAdapter(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.getPercentageAuditListQuery = SqlReader.readSql(PATH_GET_PERCENTAGE_AUDIT_LIST);
        this.insertPercentageAuditCommand = SqlReader.readSql(PATH_INSERT_PERCENTAGE_AUDIT);
    }

    private void printQuery(String query, Map<String, Object> queryParams) {
        log.info("Executing query {} with params {}", query.replace("\r",""), queryParams);
    }

    private <T> T handleExceptions(Supplier<T> callable) {
        try {
            return callable.get();
        } catch (EmptyResultDataAccessException ex) {
            log.error("Error finding Transaction", ex);
            throw new EntityNotFoundException(ErrorCode.REPOSITORY_NOT_FOUND);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error saving Transaction", ex);
            throw new EntityConflictException(ErrorCode.REPOSITORY_CONFLICT);
        } catch (DataAccessException ex) {
            log.error("Unexpected error saving Transaction", ex);
            throw new RepositoryNotAvailableException(ErrorCode.REPOSITORY_NOT_AVAILABLE);
        } catch (Exception ex) {
            log.error("Unexpected error saving Transaction", ex);
            throw new RuntimeException("Unexpected error");
        }
    }

    @Override
    @Async
    public void save(String uri, Object responseBody, Integer responseHttpStatus, Long elapsedTime) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.convertValue(responseBody, JsonNode.class);

        final Map<String, Object> params = new HashMap<>();
        params.put(RESPONSE_BODY, response.toString());
        params.put(URI, uri);
        params.put(RESPONSE_STATUS, responseHttpStatus);
        params.put(ELAPSED_TIME, elapsedTime);

        printQuery(insertPercentageAuditCommand, params);
        this.handleExceptions(() -> jdbcTemplate.update(insertPercentageAuditCommand, params));
    }

    @Override
    public Page<Auditory> findAll(Integer page, Integer size) {

        Integer totalElements = getTotalElements();
        Integer totalPages = 0;
        String query;

        if(page != null && size != null){
            totalPages = totalElements == 0 ? 0 : totalElements / size;
            query = getPaginatedQuery(page, size);
        } else {
            query = getQuery();
        }


        List<AuditoryJdbcResponse> auditoryJdbcResponseList = handleExceptions(() ->
                jdbcTemplate.query(query, emptyMap(), new BeanPropertyRowMapper<>(AuditoryJdbcResponse.class))
        );

        return Page.<Auditory>builder()
                .content(auditoryJdbcResponseList.stream().map(it -> it.toDomain()).collect(Collectors.toList()))
                .size(size)
                .number(page)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private String getPaginatedQuery(Integer page, Integer size) {
        String paginatedQuery = JdbcQueryBuilder.buildPagedSelectQuery(getPercentageAuditListQuery, page, size);
        printQuery(paginatedQuery, null);
        return paginatedQuery;
    }

    private String getQuery() {
        String paginatedQuery = JdbcQueryBuilder.buildSelectQuery(getPercentageAuditListQuery);
        printQuery(paginatedQuery, null);
        return paginatedQuery;
    }

    private Integer getTotalElements() {
        String countQuery = JdbcQueryBuilder.buildCountQuery(getPercentageAuditListQuery);
        printQuery(countQuery, null);
        return handleExceptions(() -> jdbcTemplate.queryForObject(countQuery, emptyMap(), Integer.class));
    }
}
