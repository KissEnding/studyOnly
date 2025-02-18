package com.agri.insurance.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compensation_record")
public class CompensationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private InsurancePolicy policy;

    @Column(name = "disaster_time", nullable = false)
    private LocalDateTime disasterTime;

    @Column(name = "disaster_type", nullable = false)
    private String disasterType;

    @Column(name = "damaged_area", nullable = false)
    private BigDecimal damagedArea;

    @Column(name = "compensation_amount", nullable = false)
    private BigDecimal compensationAmount;

    @Column(name = "status")
    private Boolean status = false;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;
} 