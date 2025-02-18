package com.agri.insurance.service;

import com.agri.insurance.model.InsurancePolicy;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.repository.InsurancePolicyRepository;
import com.agri.insurance.repository.FarmerRepository;
import com.agri.insurance.dto.PolicyStatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsurancePolicyService {
    private final InsurancePolicyRepository policyRepository;
    private final FarmerRepository farmerRepository;

    @Transactional(readOnly = true)
    public List<InsurancePolicy> getPolicies(String username, String status) {
        Farmer farmer = farmerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("未找到农户信息"));
            
        if (status != null) {
            return policyRepository.findByFarmerIdAndStatus(farmer.getId(), status);
        }
        return policyRepository.findByFarmerId(farmer.getId());
    }

    @Transactional(readOnly = true)
    public InsurancePolicy getPolicy(Long id, String username) {
        Farmer farmer = farmerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("未找到农户信息"));
            
        return policyRepository.findByIdAndFarmerId(id, farmer.getId())
            .orElseThrow(() -> new RuntimeException("未找到保单信息"));
    }

    @Transactional(readOnly = true)
    public PolicyStatisticsResponse getPolicyStatistics(String username) {
        Farmer farmer = farmerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("未找到农户信息"));
            
        List<InsurancePolicy> policies = policyRepository.findByFarmerId(farmer.getId());
        
        long activePolicyCount = policies.stream()
            .filter(p -> "active".equals(p.getStatus()))
            .count();
            
        BigDecimal totalPremium = policies.stream()
            .map(InsurancePolicy::getActualPremium)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCoverage = policies.stream()
            .filter(p -> p.getProduct() != null)
            .map(p -> p.getProduct().getBasePremium()
                .multiply(p.getProduct().getCoverageMultiplier()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        return PolicyStatisticsResponse.builder()
            .policyCount(activePolicyCount)
            .totalPremium(totalPremium)
            .totalCoverage(totalCoverage)
            .build();
    }
} 