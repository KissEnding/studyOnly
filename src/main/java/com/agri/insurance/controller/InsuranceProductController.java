package com.agri.insurance.controller;

import com.agri.insurance.model.InsuranceProduct;
import com.agri.insurance.service.InsuranceProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class InsuranceProductController {
    private final InsuranceProductService productService;

    @GetMapping
    public ResponseEntity<List<InsuranceProduct>> getProducts(
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String cityCode) {
        log.info("Getting products for province: {}, city: {}", provinceCode, cityCode);
        return ResponseEntity.ok(productService.getProducts(provinceCode, cityCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceProduct> getProduct(@PathVariable Long id) {
        log.info("Getting product details: {}", id);
        return ResponseEntity.ok(productService.getProduct(id));
    }
} 