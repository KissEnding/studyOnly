package com.agri.insurance.service;

import com.agri.insurance.dto.AuditRequest;
import com.agri.insurance.dto.FarmerListResponse;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final FarmerRepository farmerRepository;

    @Transactional(readOnly = true)
    public List<FarmerListResponse> getFarmersList(String status) {
        log.info("Getting farmers list with status: {}", status);
        List<Farmer> farmers;
        
        try {
            if (status != null && !status.isEmpty()) {
                farmers = farmerRepository.findByStatus(status);
                log.info("Found {} farmers with status: {}", farmers.size(), status);
            } else {
                farmers = farmerRepository.findAll();
                log.info("Found {} total farmers", farmers.size());
            }

            List<FarmerListResponse> response = farmers.stream()
                .map(this::convertToFarmerListResponse)
                .collect(Collectors.toList());
                
            log.info("Converted {} farmers to response", response.size());
            return response;
        } catch (Exception e) {
            log.error("Error getting farmers list", e);
            throw e;
        }
    }

    private FarmerListResponse convertToFarmerListResponse(Farmer farmer) {
        try {
            FarmerListResponse response = new FarmerListResponse();
            response.setId(farmer.getId());
            response.setName(farmer.getName());
            response.setUsername(farmer.getUsername());
            response.setIdCard(farmer.getIdCard());
            response.setLocation(farmer.getLocation());
            response.setContactPhone(farmer.getContactPhone());
            response.setStatus(farmer.getStatus());
            response.setCreatedAt(farmer.getCreatedAt());
            response.setHouseholdDoc(farmer.getHouseholdDoc());
            return response;
        } catch (Exception e) {
            log.error("Error converting farmer to response: {}", farmer, e);
            throw e;
        }
    }

    @Transactional
    public void auditFarmer(Long id, AuditRequest request) {
        log.info("Processing farmer audit, id: {}", id);
        Farmer farmer = farmerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("农户不存在"));

        if (!"pending".equals(farmer.getStatus())) {
            throw new RuntimeException("只能审核待审核状态的农户");
        }

        farmer.setStatus(request.getStatus());
        farmerRepository.save(farmer);
        log.info("Farmer {} status updated to: {}", id, request.getStatus());
    }
} 