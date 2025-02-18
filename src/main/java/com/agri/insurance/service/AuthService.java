package com.agri.insurance.service;

import com.agri.insurance.dto.*;
import com.agri.insurance.model.Admin;
import com.agri.insurance.model.InsuranceCompany;
import com.agri.insurance.model.Farmer;
import com.agri.insurance.model.CompanyStatus;
import com.agri.insurance.model.CompanyType;
import com.agri.insurance.repository.AdminRepository;
import com.agri.insurance.repository.InsuranceCompanyRepository;
import com.agri.insurance.repository.FarmerRepository;
import com.agri.insurance.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}, type: {}", 
                request.getUsername(), request.getUserType());
                
        switch (request.getUserType()) {
            case "farmer":
                return handleFarmerLogin(request);
            case "company":
                return handleCompanyLogin(request);
            case "admin":
                return handleAdminLogin(request);
            default:
                throw new BadCredentialsException("无效的用户类型");
        }
    }

    private LoginResponse handleAdminLogin(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        log.info("Admin login successful: {}", admin.getUsername());
        String token = jwtUtils.generateToken(admin);
        log.info("Generated token for admin: {}", token);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserType("ADMIN");
        response.setUsername(admin.getUsername());
        response.setName(admin.getName());
        return response;
    }

    private LoginResponse handleCompanyLogin(LoginRequest request) {
        InsuranceCompany company = insuranceCompanyRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), company.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        if (company.getStatus() != CompanyStatus.approved) {
            throw new BadCredentialsException("账号未通过审核");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtils.generateToken(company));
        response.setUserType("COMPANY");
        response.setUsername(company.getUsername());
        response.setName(company.getCompanyName());
        return response;
    }

    @Transactional
    public void registerCompany(CompanyRegisterRequest request) {
        log.info("Processing company registration: {}", request.getCompanyName());
        
        // 检查用户名是否已存在
        if (insuranceCompanyRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Username already exists: {}", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        // 如果是分公司，验证总公司
        if ("branch".equals(request.getType()) && request.getParentCompany() != null) {
            InsuranceCompany parentCompany = insuranceCompanyRepository.findById(Long.parseLong(request.getParentCompany()))
                    .orElseThrow(() -> {
                        log.error("Parent company not found: {}", request.getParentCompany());
                        return new RuntimeException("总公司不存在");
                    });
            
            if (parentCompany.getType() != CompanyType.headquarters) {
                log.error("Parent company is not headquarters: {}", request.getParentCompany());
                throw new RuntimeException("只能选择总公司作为上级公司");
            }
        }

        InsuranceCompany company = new InsuranceCompany();
        company.setCompanyName(request.getCompanyName());
        company.setCreditCode(request.getCreditCode());
        company.setType(CompanyType.valueOf(request.getType()));
        company.setParentId(request.getParentCompany() != null ? Long.parseLong(request.getParentCompany()) : null);
        
        // 组合城市和区县代码为location
        String location = String.format("%s%s", request.getCityCode(), request.getDistrictCode());
        company.setLocation(location);
        
        company.setContactPhone(request.getContactPhone());
        company.setUsername(request.getUsername());
        company.setPassword(passwordEncoder.encode(request.getPassword()));
        company.setStatus(CompanyStatus.pending);
        company.setCreatedAt(LocalDateTime.now());

        insuranceCompanyRepository.save(company);
        log.info("Company registration completed: {}", request.getCompanyName());
    }

    @Transactional
    public void registerFarmer(FarmerRegisterRequest request) {
        log.info("Processing farmer registration for username: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (farmerRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建农户实体
        Farmer farmer = new Farmer();
        farmer.setUsername(request.getUsername());
        farmer.setPassword(passwordEncoder.encode(request.getPassword()));
        farmer.setName(request.getName());
        farmer.setIdCard(request.getIdCard());
        farmer.setLocation(request.getLocation());
        farmer.setContactPhone(request.getContactPhone());
        farmer.setHouseholdDoc(request.getHouseholdDoc());
        farmer.setCreatedAt(LocalDateTime.now());

        // 保存到数据库
        farmerRepository.save(farmer);
        log.info("Farmer registered successfully: {}", request.getUsername());
    }

    public AdminLoginResponse adminLogin(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = jwtUtils.generateToken(admin);

        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setName(admin.getName());
        response.setRole("ROLE_ADMIN");
        
        return response;
    }

    private LoginResponse handleFarmerLogin(LoginRequest request) {
        log.info("Handling farmer login for: {}", request.getUsername());
        
        Farmer farmer = farmerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
            log.warn("Password mismatch for farmer: {}", request.getUsername());
            throw new BadCredentialsException("用户名或密码错误");
        }

        if (!"approved".equals(farmer.getStatus())) {
            throw new BadCredentialsException("账号未通过审核");
        }

        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtils.generateToken(farmer));
        response.setUserType("FARMER");
        response.setUsername(farmer.getUsername());
        response.setName(farmer.getName());

        log.info("Farmer login successful: {}", request.getUsername());
        return response;
    }
} 