package com.agri.insurance.service;

import com.agri.insurance.dto.AuditRequest;
import com.agri.insurance.dto.FarmerListResponse;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerService {
    private final FarmerRepository farmerRepository;

    public List<FarmerListResponse> getFarmerList(String status) {
        List<Farmer> farmers;
        if (status != null) {
            farmers = farmerRepository.findByStatus(status);
        } else {
            farmers = farmerRepository.findAll();
        }
        
        return farmers.stream()
            .map(this::toListResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void auditFarmer(Long id, AuditRequest request, String auditor) {
        Farmer farmer = farmerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("农户不存在"));

        farmer.setStatus(request.getStatus());
        

        farmerRepository.save(farmer);
        log.info("Farmer {} audit completed: {}", id, request.getStatus());
    }

    @Transactional(readOnly = true)
    public Farmer getFarmerProfile(String username) {
        log.info("Getting farmer profile for username: {}", username);
        return farmerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("农户不存在"));
    }

    @Transactional(readOnly = true)
    public Farmer getCurrentFarmer(String username) {
        return farmerRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在或未登录"));
    }

    private FarmerListResponse toListResponse(Farmer farmer) {
        FarmerListResponse response = new FarmerListResponse();
        BeanUtils.copyProperties(farmer, response);
        return response;
    }
} 