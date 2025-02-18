package com.agri.insurance.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class InsuranceProductResponse {
    private Long id;
    private String productCode;
    private String productName;
    
    // 农作物信息
    private Set<CropInfo> crops;
    
    // 地区信息
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    
    // 其他字段...
    
    @Data
    public static class CropInfo {
        private Long id;
        private String cropCode;
        private String cropName;
        private String category;
    }
} 