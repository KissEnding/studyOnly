package com.agri.insurance.controller;

import com.agri.insurance.model.InsurancePolicy;
import com.agri.insurance.service.InsurancePolicyService;
import com.agri.insurance.dto.PolicyStatisticsResponse;
import com.agri.insurance.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class InsurancePolicyController {
    private final InsurancePolicyService policyService;

    @GetMapping
    public Result<List<InsurancePolicy>> getPolicies(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal String username) {
        log.info("Getting policies for user: {}, status: {}", username, status);
        return Result.success(policyService.getPolicies(username, status));
    }

    @GetMapping("/{id}")
    public Result<InsurancePolicy> getPolicy(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        log.info("Getting policy details, id: {}, username: {}", id, username);
        return Result.success(policyService.getPolicy(id, username));
    }

    @GetMapping("/statistics")
    public Result<PolicyStatisticsResponse> getPolicyStatistics(
            @AuthenticationPrincipal String username) {
        log.info("Getting policy statistics for user: {}", username);
        return Result.success(policyService.getPolicyStatistics(username));
    }
} 