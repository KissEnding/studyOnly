package com.agri.insurance.controller;

import com.agri.insurance.dto.CompanyInfoResponse;
import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.service.InsuranceCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class CompanyController {
    private final InsuranceCompanyService companyService;

    @GetMapping("/info")
    public ResponseEntity<CompanyInfoResponse> getCompanyInfo() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Current authentication: {}", auth);
            if (auth == null) {
                log.error("No authentication found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = auth.getName();
            log.info("Getting company info for username: {}", username);
            
            InsuranceCompany company = companyService.getCompanyByUsername(username);
            log.info("Found company: {}", company);
            
            return ResponseEntity.ok(CompanyInfoResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .creditCode(company.getCreditCode())
                .contactPerson(company.getContactPerson())
                .contactPhone(company.getContactPhone())
                .location(company.getLocation())
                .type(company.getType())
                .status(company.getStatus())
                .registeredCapital(company.getRegisteredCapital())
                .build());
        } catch (Exception e) {
            log.error("Failed to get company info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CompanyInfoResponse.builder()
                    .error(e.getMessage())
                    .build());
        }
    }
} 