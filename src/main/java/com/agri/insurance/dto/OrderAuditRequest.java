package com.agri.insurance.dto;

import lombok.Data;

@Data
public class OrderAuditRequest {
    private String action;  // "approve" 或 "reject"
    private String remarks; // 审核备注
} 