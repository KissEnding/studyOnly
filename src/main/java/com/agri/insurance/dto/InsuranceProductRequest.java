package com.agri.insurance.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class InsuranceProductRequest {
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    private String description;

    @NotNull(message = "基础保费不能为空")
    @DecimalMin(value = "0.01", message = "基础保费必须大于0")
    private BigDecimal basePremium;

    @NotNull(message = "保险金额倍数不能为空")
    @DecimalMin(value = "1.0", message = "保险金额倍数必须大于等于1")
    private BigDecimal coverageMultiplier;

    @NotNull(message = "起保日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "到期日期不能为空")
    private LocalDate endDate;

    private String provinceCode;

    private String cityCode;

    @NotEmpty(message = "承保作物不能为空")
    private Set<Long> cropIds;
} 