package com.agri.insurance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "insurance_product")
public class InsuranceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private InsuranceCompany company;

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "crop_type", nullable = false, length = 50)
    private String cropType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "stock_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal stockAmount;

    @Column(name = "base_premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "coverage_multiplier", nullable = false, precision = 19, scale = 2)
    private BigDecimal coverageMultiplier;

    @Column(name = "total_coverage", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCoverage;

    @Column(name = "used_coverage", nullable = false, precision = 15, scale = 2)
    private BigDecimal usedCoverage;

    @Column(name = "province_code", length = 6)
    private String provinceCode;

    @Column(name = "city_code", length = 6)
    private String cityCode;

    @Column(name = "district_code", length = 6)
    private String districtCode;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.ALL;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "name", nullable = false)
    private String name;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 