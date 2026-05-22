package com.banking.transaction.client;

import com.banking.transaction.client.dto.AccountFeignResponse;
import com.banking.transaction.client.dto.AmountFeignRequest;
import com.banking.transaction.client.dto.BalanceFeignResponse;
import com.banking.transaction.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", configuration = FeignConfig.class)
public interface AccountClient {

    @GetMapping("/api/v1/accounts/{accountId}/balance")
    BalanceFeignResponse getBalance(@PathVariable("accountId") UUID accountId);

    @GetMapping("/api/v1/accounts/{accountId}")
    AccountFeignResponse getAccount(@PathVariable("accountId") UUID accountId);

    @PostMapping("/api/v1/accounts/{accountId}/withdraw")
    AccountFeignResponse withdraw(@PathVariable("accountId") UUID accountId, @RequestBody AmountFeignRequest request);

    @PostMapping("/api/v1/accounts/{accountId}/deposit")
    AccountFeignResponse deposit(@PathVariable("accountId") UUID accountId, @RequestBody AmountFeignRequest request);
}
