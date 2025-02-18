package com.agri.insurance.controller;

import com.agri.insurance.model.InsuranceOrder;
import com.agri.insurance.service.InsuranceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class InsuranceOrderController {
    private final InsuranceOrderService orderService;

    @GetMapping
    public ResponseEntity<List<InsuranceOrder>> getOrders(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal String username) {
        log.info("Getting orders for user: {}, status: {}", username, status);
        return ResponseEntity.ok(orderService.getOrders(username, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceOrder> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        log.info("Getting order details, id: {}, username: {}", id, username);
        return ResponseEntity.ok(orderService.getOrder(id, username));
    }
} 