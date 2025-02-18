package com.agri.insurance.repository;

import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.model.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, Long> {
    @Query("SELECT p FROM InsuranceProduct p LEFT JOIN FETCH p.company WHERE p.visible = true")
    List<InsuranceProduct> findByVisibleTrueWithCompany();

    @Query("SELECT p FROM InsuranceProduct p LEFT JOIN FETCH p.company WHERE p.visible = true AND p.provinceCode = ?1")
    List<InsuranceProduct> findByVisibleTrueAndProvinceCodeWithCompany(String provinceCode);

    @Query("SELECT p FROM InsuranceProduct p LEFT JOIN FETCH p.company WHERE p.visible = true AND p.cityCode = ?1")
    List<InsuranceProduct> findByVisibleTrueAndCityCodeWithCompany(String cityCode);

    @Query("SELECT p FROM InsuranceProduct p LEFT JOIN FETCH p.company WHERE p.id = ?1")
    Optional<InsuranceProduct> findByIdWithCompany(Long id);

    List<InsuranceProduct> findByVisible(Boolean visible);
    List<InsuranceProduct> findByVisibleTrue();
    List<InsuranceProduct> findByVisibleTrueAndProvinceCode(String provinceCode);
    List<InsuranceProduct> findByVisibleTrueAndCityCode(String cityCode);
    List<InsuranceProduct> findByVisibleAndProvinceCode(Boolean visible, String provinceCode);
    List<InsuranceProduct> findByVisibleAndCityCode(Boolean visible, String cityCode);
    List<InsuranceProduct> findByCompanyAndVisible(InsuranceCompany company, Boolean visible);
    Optional<InsuranceProduct> findByIdAndCompany(Long id, InsuranceCompany company);
    Optional<InsuranceProduct> findByProductCode(String productCode);
    List<InsuranceProduct> findByCompany(InsuranceCompany company);
} 