package com.agri.insurance.controller;

import com.agri.insurance.model.*;
import com.agri.insurance.repository.InsuranceCompanyRepository;
import com.agri.insurance.repository.InsuranceProductRepository;
import com.agri.insurance.service.InsuranceOrderService;
import com.agri.insurance.service.InsuranceProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class InsuranceMarketController {
    private final InsuranceProductService productService;
    private final InsuranceOrderService orderService;
    private final InsuranceCompanyRepository companyRepository;
    private final InsuranceProductRepository productRepository;

    @GetMapping("/products")
    public ResponseEntity<List<InsuranceProduct>> getProducts(
            @RequestParam(required = false) String provinceCode,
            @RequestParam(required = false) String cityCode) {
        log.info("Getting products for province: {}, city: {}", provinceCode, cityCode);
        return ResponseEntity.ok(productService.getProducts(provinceCode, cityCode));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<InsuranceProduct> getProduct(@PathVariable Long id) {
        log.info("Getting product details: {}", id);
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping("/orders")
    public ResponseEntity<InsuranceOrder> createOrder(
            @RequestParam Long productId,
            @RequestParam Double area,
            @AuthenticationPrincipal String username) {
        log.info("Creating order for product: {}, area: {}, username: {}", productId, area, username);
        return ResponseEntity.ok(orderService.createOrder(productId, username, area));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<InsuranceOrder>> getOrders(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal String username) {
        log.info("Getting orders for user: {}, status: {}", username, status);
        return ResponseEntity.ok(orderService.getOrders(username, status));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<InsuranceOrder> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        log.info("Getting order details: {}, username: {}", id, username);
        return ResponseEntity.ok(orderService.getOrder(id, username));
    }

    @GetMapping("/test-data")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> createTestData() {
        try {
            InsuranceCompany company = new InsuranceCompany();
            company.setCompanyName("浙江农业保险公司");
            company.setUsername("test_company");
            company.setPassword("123456");
            company.setContactPerson("张三");
            company.setContactPhone("13800138000");
            company.setLocation("浙江省杭州市");
            company.setType(CompanyType.headquarters);
            company.setStatus(CompanyStatus.approved);
            company.setCreditCode("91110000123456789X");
            company.setRegisteredCapital(new BigDecimal("1000000000"));
            company = companyRepository.save(company);

            InsuranceProduct product = new InsuranceProduct();
            product.setProductCode("TEST001");
            product.setProductName("果树种植保险");
            product.setDescription("这是一个果树种植保险产品，提供全面的果树种植保障");
            product.setCropType("果树");
            product.setBasePremium(new BigDecimal("120"));
            product.setCoverageMultiplier(new BigDecimal("2.0"));
            product.setStockAmount(new BigDecimal("60000000"));
            product.setTotalCoverage(new BigDecimal("60000000"));
            product.setUsedCoverage(BigDecimal.ZERO);
            product.setStartDate(LocalDate.of(2024, 12, 31));
            product.setEndDate(LocalDate.of(2025, 12, 31));
            product.setProvinceCode("330000");  // 浙江省
            product.setCityCode("330100");      // 杭州市
            product.setVisible(true);
            product.setStatus("approved");
            product.setUserType(UserType.ALL);
            product.setCompany(company);
            productRepository.save(product);

            return ResponseEntity.ok("测试数据创建成功");
        } catch (Exception e) {
            log.error("Failed to create test data", e);
            return ResponseEntity.internalServerError().body("创建测试数据失败: " + e.getMessage());
        }
    }
} 