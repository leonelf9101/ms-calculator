package com.calculator.adapter.controller;

import com.calculator.adapter.controller.model.AuditoryResponse;
import com.calculator.adapter.controller.model.PageResponse;
import com.calculator.application.port.in.GetAuditoryListQuery;
import com.calculator.domain.Auditory;
import com.calculator.domain.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuditoryController {
    private final GetAuditoryListQuery getAuditoryListUseCase;

    public AuditoryController(GetAuditoryListQuery getAuditoryListUseCase) {
        this.getAuditoryListUseCase = getAuditoryListUseCase;
    }

    @GetMapping("/calculations/audits")
    @ApiOperation("Get auditory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request processed correctly"),
            @ApiResponse(code = 400, message = "Malformed request syntax"),
            @ApiResponse(code = 401, message = "Invalid token"),
            @ApiResponse(code = 403, message = "Permission denied"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable"),})
    public PageResponse<AuditoryResponse> getCalculatedValue(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {

        GetAuditoryListQuery.Data data =
                GetAuditoryListQuery.Data.builder()
                        .page(page)
                        .size(size)
                        .build();

        Page<Auditory> auditoryList = getAuditoryListUseCase.execute(data);

        return new PageResponse<AuditoryResponse>().fromDomain(auditoryList, AuditoryResponse::fromDomain);
    }
}
