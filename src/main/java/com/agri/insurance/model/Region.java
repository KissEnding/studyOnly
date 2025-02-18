package com.agri.insurance.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 20, nullable = false)
    private String code;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "parent_code", length = 20)
    private String parentCode;
    
    @Column(nullable = false, length = 20)
    private String level;  // 级别："province", "city", "district"
} 