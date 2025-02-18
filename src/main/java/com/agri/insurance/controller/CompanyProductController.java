package com.agri.insurance.controller;

import com.agri.insurance.model.InsuranceProduct;
import com.agri.insurance.service.InsuranceProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/company/products")
@RequiredArgsConstructor
public class CompanyProductController {
    private final InsuranceProductService productService;

    @GetMapping
    public ResponseEntity<List<InsuranceProduct>> getProducts(
            @RequestParam(required = false) Boolean visible,
            @AuthenticationPrincipal String username) {
        log.info("Getting company products: visible={}, username={}", visible, username);
        return ResponseEntity.ok(productService.getCompanyProducts(username, visible));
    }

    @PostMapping
    public ResponseEntity<InsuranceProduct> createProduct(
            @RequestBody InsuranceProduct product,
            @AuthenticationPrincipal String username) {
        log.info("Creating product: {}, username: {}", product, username);
        return ResponseEntity.ok(productService.createProduct(product, username));
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<Void> toggleProductVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        log.info("Toggling product visibility: {}, username: {}", id, username);
        productService.toggleProductVisibility(id, username);
        return ResponseEntity.ok().build();
    }
} 