package com.agri.insurance.service;

import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.repository.InsuranceCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceCompanyService {

    private final InsuranceCompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public InsuranceCompany getCompanyByUsername(String username) {
        log.info("Getting company info for user: {}", username);
        
        log.debug("Executing findByUsername query with username: {}", username);
        Optional<InsuranceCompany> companyOpt = companyRepository.findByUsername(username);
        
        if (!companyOpt.isPresent()) {
            log.warn("No company found for username: {}", username);
            throw new RuntimeException("未找到公司信息");
        }
        
        InsuranceCompany company = companyOpt.get();
        log.info("Found company: id={}, companyName={}", company.getId(), company.getCompanyName());
        
        return company;
    }

    public InsuranceCompany getCurrentCompany() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getCompanyByUsername(username);
    }
} 