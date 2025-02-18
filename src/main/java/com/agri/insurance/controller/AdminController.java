package com.agri.insurance.controller;

import com.agri.insurance.dto.AuditRequest;
import com.agri.insurance.dto.FarmerListResponse;
import com.agri.insurance.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerListResponse>> getFarmers(
            @RequestParam(required = false) String status) {
        log.info("Getting farmers list with status: {}", status);
        List<FarmerListResponse> farmers = adminService.getFarmersList(status);
        return ResponseEntity.ok(farmers);
    }

    @PostMapping("/farmers/{id}/audit")
    public ResponseEntity<?> auditFarmer(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        log.info("Processing farmer audit, id: {}, status: {}", id, request.getStatus());
        adminService.auditFarmer(id, request);
        return ResponseEntity.ok().build();
    }
} 