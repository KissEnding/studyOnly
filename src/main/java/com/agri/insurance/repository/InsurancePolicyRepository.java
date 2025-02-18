package com.agri.insurance.repository;

import com.agri.insurance.model.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    List<InsurancePolicy> findByFarmerId(Long farmerId);
    List<InsurancePolicy> findByFarmerIdAndStatus(Long farmerId, String status);
    Optional<InsurancePolicy> findByIdAndFarmerId(Long id, Long farmerId);
} 