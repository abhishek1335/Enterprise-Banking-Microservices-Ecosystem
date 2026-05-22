package com.banking.fraud.rule;

import com.banking.fraud.entity.FraudRuleCode;
import com.banking.fraud.entity.FraudSeverity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudRuleResult {

    private FraudRuleCode ruleCode;
    private FraudSeverity severity;
    private String description;
}
