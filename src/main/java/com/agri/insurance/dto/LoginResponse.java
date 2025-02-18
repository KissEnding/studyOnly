package com.agri.insurance.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userType;
    private String username;
    private String name;
} 