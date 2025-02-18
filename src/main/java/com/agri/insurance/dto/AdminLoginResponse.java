package com.agri.insurance.dto;

import lombok.Data;

@Data
public class AdminLoginResponse {
    private String token;
    private String name;
    private String role;
} 