package com.agri.insurance.controller;

import com.agri.insurance.dto.ErrorResponse;
import com.agri.insurance.dto.AdminLoginRequest;
import com.agri.insurance.dto.AdminLoginResponse;
import com.agri.insurance.dto.FarmerRegisterRequest;
import com.agri.insurance.dto.LoginRequest;
import com.agri.insurance.dto.LoginResponse;
import com.agri.insurance.dto.CompanyRegisterRequest;
import com.agri.insurance.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/farmer")
    public ResponseEntity<?> registerFarmer(@Valid @RequestBody FarmerRegisterRequest request) {
        try {
            log.info("Received farmer registration request: {}", request.getUsername());
            authService.registerFarmer(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to register farmer: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
        try {
            log.info("Received company registration request: {}", request.getCompanyName());
            authService.registerCompany(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to register company: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        log.info("Admin login attempt: {}", request.getUsername());
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.info("Login request received for user: {}, type: {}", request.getUsername(), request.getUserType());
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("登录失败：" + e.getMessage()));
        }
    }

    // 添加一个测试接口
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth API is working");
    }
} 