package com.calculator.adapter.rest;

import com.calculator.adapter.rest.handler.RestTemplateErrorHandler;
import com.calculator.application.exception.EntityBadRequestException;
import com.calculator.application.exception.EntityConflictException;
import com.calculator.application.exception.EntityNotFoundException;
import com.calculator.application.exception.RepositoryNotAvailableException;
import com.calculator.application.port.out.PercentageAuditRepository;
import com.calculator.application.port.out.PercentageRepository;
import com.calculator.config.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class PercentageRestAdapter implements PercentageRepository {

	private final RestTemplate restTemplate;
	private final String pathGetPercentage;
	private final CacheManager cacheManager;
	private final PercentageAuditRepository percentageAuditJdbcAdapter;

	public PercentageRestAdapter(
			RestTemplate restTemplate,
			CacheManager cacheManager,
			PercentageAuditRepository percentageAuditJdbcAdapter,
			@Value ("${percentage.url}") String pathGetPercentage) {
		this.percentageAuditJdbcAdapter = percentageAuditJdbcAdapter;
		this.restTemplate = restTemplate;
		this.cacheManager = cacheManager;
		this.pathGetPercentage = pathGetPercentage;

		var errorHandler = new RestTemplateErrorHandler(
				Map.of(HttpStatus.BAD_REQUEST, new EntityBadRequestException(ErrorCode.REPOSITORY_BAD_REQUEST),
					   HttpStatus.NOT_FOUND, new EntityNotFoundException(ErrorCode.REPOSITORY_NOT_FOUND),
					   HttpStatus.CONFLICT, new EntityConflictException(ErrorCode.REPOSITORY_CONFLICT),
					   HttpStatus.INTERNAL_SERVER_ERROR, new RepositoryNotAvailableException(ErrorCode.REPOSITORY_NOT_AVAILABLE),
					   HttpStatus.SERVICE_UNAVAILABLE, new RepositoryNotAvailableException(ErrorCode.REPOSITORY_NOT_AVAILABLE)
				));

		this.restTemplate.setErrorHandler(errorHandler);
	}

	@Override
	@Cacheable(value = "percentage", cacheManager = "percentageCacheManager")
	@Retryable(maxAttempts= 3, value = RuntimeException.class, backoff = @Backoff(delay = 5000, multiplier = 1))
	public BigDecimal getPercentage() {
		String uri = UriComponentsBuilder.fromHttpUrl(pathGetPercentage).toUriString();
		log.info("Invoking GET to URI : {}", uri);
		ResponseEntity<JsonNode> result = restTemplate.exchange(uri, HttpMethod.GET, null, JsonNode.class);

		JsonNode body = result.getBody();

		BigDecimal percentage = body.get("percentage").decimalValue();

		cacheManager.getCache("lastPercentageReturned").put("lastPercentageReturned", percentage);
		return percentage;
	}

}
