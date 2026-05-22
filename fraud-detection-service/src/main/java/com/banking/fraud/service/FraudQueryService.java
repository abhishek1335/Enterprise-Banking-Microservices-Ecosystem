package com.banking.fraud.service;

import com.banking.common.exception.BusinessException;
import com.banking.fraud.dto.response.FraudAlertResponse;
import com.banking.fraud.mapper.FraudAlertMapper;
import com.banking.fraud.repository.FraudAlertRepository;
import com.banking.fraud.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudQueryService {

    private final FraudAlertRepository fraudAlertRepository;

    @Transactional(readOnly = true)
    public List<FraudAlertResponse> myAlerts() {
        return fraudAlertRepository.findByUserIdOrderByCreatedAtDesc(currentUserId()).stream()
                .map(FraudAlertMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FraudAlertResponse getAlert(UUID alertId) {
        return fraudAlertRepository.findByIdAndUserId(alertId, currentUserId())
                .map(FraudAlertMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Fraud alert not found", HttpStatus.NOT_FOUND));
    }

    private UUID currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        throw new IllegalStateException("Unauthorized");
    }
}
