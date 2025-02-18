package com.agri.insurance.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class PolicyStatisticsResponse {
    private long policyCount;
    private BigDecimal totalPremium;
    private BigDecimal totalCoverage;
} 