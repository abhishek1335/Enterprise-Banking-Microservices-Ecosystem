package com.banking.fraud.controller;

import com.banking.common.constants.ServiceConstants;
import com.banking.common.dto.ApiResponse;
import com.banking.fraud.dto.response.FraudAlertResponse;
import com.banking.fraud.service.FraudQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ServiceConstants.FRAUD_API_PREFIX)
@RequiredArgsConstructor
@Tag(name = "Fraud")
@SecurityRequirement(name = "bearerAuth")
public class FraudController {

    private final FraudQueryService queryService;

    @GetMapping("/me")
    @Operation(summary = "List fraud alerts for the logged-in user")
    public ApiResponse<List<FraudAlertResponse>> myAlerts() {
        return ApiResponse.success(queryService.myAlerts());
    }

    @GetMapping("/alerts/{alertId}")
    @Operation(summary = "Get a fraud alert by ID")
    public ApiResponse<FraudAlertResponse> getAlert(@PathVariable UUID alertId) {
        return ApiResponse.success(queryService.getAlert(alertId));
    }
}
