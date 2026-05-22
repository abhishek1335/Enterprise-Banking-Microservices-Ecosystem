package com.banking.account.controller;

import com.banking.account.dto.request.AmountRequest;
import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.dto.response.AccountResponse;
import com.banking.account.dto.response.BalanceResponse;
import com.banking.account.service.AccountService;
import com.banking.common.constants.ServiceConstants;
import com.banking.common.dto.ApiResponse;
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
@RequestMapping(ServiceConstants.ACCOUNT_API_PREFIX)
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new bank account for the logged-in user")
    public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResponse.success("Account created", accountService.createAccount(request));
    }

    @GetMapping
    @Operation(summary = "List all accounts for the logged-in user")
    public ApiResponse<List<AccountResponse>> listAccounts() {
        return ApiResponse.success(accountService.listMyAccounts());
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details by ID")
    public ApiResponse<AccountResponse> getAccount(@PathVariable UUID accountId) {
        return ApiResponse.success(accountService.getAccount(accountId));
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get current balance")
    public ApiResponse<BalanceResponse> getBalance(@PathVariable UUID accountId) {
        return ApiResponse.success(accountService.getBalance(accountId));
    }

    @PostMapping("/{accountId}/deposit")
    @Operation(summary = "Deposit money into account")
    public ApiResponse<AccountResponse> deposit(
            @PathVariable UUID accountId,
            @Valid @RequestBody AmountRequest request) {
        return ApiResponse.success("Deposit successful", accountService.deposit(accountId, request));
    }

    @PostMapping("/{accountId}/withdraw")
    @Operation(summary = "Withdraw money from account")
    public ApiResponse<AccountResponse> withdraw(
            @PathVariable UUID accountId,
            @Valid @RequestBody AmountRequest request) {
        return ApiResponse.success("Withdrawal successful", accountService.withdraw(accountId, request));
    }
}
