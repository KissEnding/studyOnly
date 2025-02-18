package com.agri.insurance.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FarmerListResponse {
    private Long id;
    private String name;
    private String username;
    private String idCard;
    private String location;
    private String contactPhone;
    private String status;
    private LocalDateTime createdAt;
    private String householdDoc;
    private LocalDateTime auditTime;
    private String auditComment;
    private String auditor;
} 