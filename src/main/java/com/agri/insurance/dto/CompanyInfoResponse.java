package com.agri.insurance.dto;

import com.agri.insurance.model.CompanyStatus;
import com.agri.insurance.model.CompanyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CompanyInfoResponse {
    private Long id;
    private String companyName;
    private String creditCode;
    private String contactPerson;
    private String contactPhone;
    private String location;
    private CompanyType type;
    private CompanyStatus status;
    private BigDecimal registeredCapital;
    private String error;
} 