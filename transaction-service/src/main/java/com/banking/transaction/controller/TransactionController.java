package com.banking.transaction.controller;

import com.banking.common.constants.ServiceConstants;
import com.banking.common.dto.ApiResponse;
import com.banking.transaction.dto.request.TransferRequest;
import com.banking.transaction.dto.response.TransactionResponse;
import com.banking.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ServiceConstants.TRANSACTION_API_PREFIX)
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transfers and transaction history")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Transfer money between two accounts")
    public ApiResponse<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ApiResponse.success("Transfer completed", transactionService.transfer(request));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID")
    public ApiResponse<TransactionResponse> getTransaction(@PathVariable UUID transactionId) {
        return ApiResponse.success(transactionService.getById(transactionId));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Transaction history for an account")
    public ApiResponse<List<TransactionResponse>> accountHistory(@PathVariable UUID accountId) {
        return ApiResponse.success(transactionService.historyByAccount(accountId));
    }

    @GetMapping("/me")
    @Operation(summary = "All transactions initiated by current user")
    public ApiResponse<List<TransactionResponse>> myTransactions() {
        return ApiResponse.success(transactionService.myTransactions());
    }
}
