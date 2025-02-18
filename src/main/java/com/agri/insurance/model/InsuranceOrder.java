package com.agri.insurance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "insurance_orders")
public class InsuranceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code")
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private InsuranceProduct product;

    private BigDecimal area;
    private BigDecimal premium;
    private BigDecimal coverage;
    private String address;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    private String status;
    private String remarks;
    
    @Column(name = "audit_remarks")
    private String auditRemarks;
    
    @Column(name = "audit_time")
    private LocalDateTime auditTime;
    
    @Column(name = "created_at")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public String getCompanyUsername() {
        return this.product != null && this.product.getCompany() != null ? 
               this.product.getCompany().getUsername() : null;
    }
} 