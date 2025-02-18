package com.agri.insurance.service;

import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.model.InsuranceProduct;
import com.agri.insurance.repository.InsuranceCompanyRepository;
import com.agri.insurance.repository.InsuranceProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceProductService {
    private final InsuranceProductRepository productRepository;
    private final InsuranceCompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<InsuranceProduct> getProducts(String provinceCode, String cityCode) {
        log.info("Fetching products with provinceCode: {}, cityCode: {}", provinceCode, cityCode);
        List<InsuranceProduct> products;
        if (cityCode != null && !cityCode.isEmpty()) {
            log.info("Searching by city code");
            products = productRepository.findByVisibleTrueAndCityCodeWithCompany(cityCode);
        } else if (provinceCode != null && !provinceCode.isEmpty()) {
            log.info("Searching by province code");
            products = productRepository.findByVisibleTrueAndProvinceCodeWithCompany(provinceCode);
        } else {
            log.info("Searching all visible products");
            products = productRepository.findByVisibleTrueWithCompany();
        }
        log.info("Found {} products", products.size());
        return products;
    }

    @Transactional(readOnly = true)
    public List<InsuranceProduct> getProducts(Boolean visible, String provinceCode, String cityCode) {
        if (visible != null) {
            if (cityCode != null && !cityCode.isEmpty()) {
                return productRepository.findByVisibleAndCityCode(visible, cityCode);
            } else if (provinceCode != null && !provinceCode.isEmpty()) {
                return productRepository.findByVisibleAndProvinceCode(visible, provinceCode);
            }
            return productRepository.findByVisible(visible);
        }
        return getProducts(provinceCode, cityCode);
    }

    @Transactional(readOnly = true)
    public List<InsuranceProduct> getCompanyProducts(String username, Boolean visible) {
        InsuranceCompany company = companyRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        if (visible != null) {
            return productRepository.findByCompanyAndVisible(company, visible);
        }
        return productRepository.findByCompany(company);
    }

    @Transactional(readOnly = true)
    public InsuranceProduct getProduct(Long id) {
        return productRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public InsuranceProduct createProduct(InsuranceProduct product, String username) {
        InsuranceCompany company = companyRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        product.setCompany(company);
        
        if (product.getProductName() == null) {
            throw new RuntimeException("产品名称不能为空");
        }
        
        if (product.getName() == null) {
            product.setName(product.getProductName());
        }
        
        if (product.getCropType() == null) {
            throw new RuntimeException("作物类型不能为空");
        }
        
        if (product.getBasePremium() == null) {
            throw new RuntimeException("基础保费不能为空");
        }
        
        if (product.getCoverageMultiplier() == null) {
            product.setCoverageMultiplier(new BigDecimal("2.0"));
        }
        
        if (product.getStockAmount() == null) {
            product.setStockAmount(new BigDecimal("1000000"));
        }
        
        if (product.getTotalCoverage() == null) {
            product.setTotalCoverage(product.getStockAmount());
        }
        
        product.setUsedCoverage(BigDecimal.ZERO);
        
        if (product.getStartDate() == null) {
            throw new RuntimeException("开始日期不能为空");
        }
        
        if (product.getEndDate() == null) {
            throw new RuntimeException("结束日期不能为空");
        }
        
        if (product.getStatus() == null) {
            product.setStatus("PENDING");
        }
        
        product.setProductCode(generateProductCode());
        product.setVisible(true);
        
        return productRepository.save(product);
    }

    private String generateProductCode() {
        return "PRD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", (int)(Math.random() * 10000));
    }

    @Transactional
    public void toggleProductVisibility(Long id, String username) {
        InsuranceProduct product = getProduct(id);
        if (!product.getCompany().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized to modify this product");
        }
        product.setVisible(!product.getVisible());
        productRepository.save(product);
    }
} 