package com.agri.insurance.controller;

import com.agri.insurance.common.Result;
import com.agri.insurance.dto.OrderAuditRequest;
import com.agri.insurance.model.InsuranceOrder;
import com.agri.insurance.service.InsuranceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/orders")
public class CompanyOrderController {
    private final InsuranceOrderService orderService;

    @GetMapping
    public Result<List<InsuranceOrder>> getOrders(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.getCompanyOrders(username, status));
    }

    @PostMapping("/{id}/audit")
    public Result<Void> auditOrder(
            @PathVariable Long id,
            @RequestBody OrderAuditRequest request,
            @AuthenticationPrincipal String username) {
        orderService.auditOrder(id, request, username);
        return Result.success(null);
    }
} 