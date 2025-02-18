package com.agri.insurance.service;

import com.agri.insurance.model.Admin;
import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.repository.AdminRepository;
import com.agri.insurance.repository.InsuranceCompanyRepository;
import com.agri.insurance.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final FarmerRepository farmerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先尝试从管理员表中查找
        if (adminRepository.findByUsername(username).isPresent()) {
            return adminRepository.findByUsername(username).get();
        }
        
        // 再尝试从公司表中查找
        if (insuranceCompanyRepository.findByUsername(username).isPresent()) {
            return insuranceCompanyRepository.findByUsername(username).get();
        }
        
        // 最后尝试从农户表中查找
        return farmerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
} 