package com.agri.insurance.controller;

import com.agri.insurance.model.Farmer;
import com.agri.insurance.service.FarmerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/farmer")
@CrossOrigin
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @GetMapping("/profile")
    public ResponseEntity<Farmer> getProfile(@AuthenticationPrincipal String username) {
        log.info("Getting profile for farmer: {}", username);
        try {
            Farmer farmer = farmerService.getFarmerProfile(username);
            log.info("Found farmer profile: {}", farmer);
            return ResponseEntity.ok(farmer);
        } catch (Exception e) {
            log.error("Error getting farmer profile", e);
            throw e;
        }
    }
} 