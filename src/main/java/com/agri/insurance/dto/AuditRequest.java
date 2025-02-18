package com.agri.insurance.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuditRequest {
    @NotBlank(message = "审核结果不能为空")
    private String status;
} 