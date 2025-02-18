package com.agri.insurance.dto;

import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class InsuranceApplicationRequest {
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "投保面积不能为空")
    @DecimalMin(value = "0.01", message = "投保面积必须大于0")
    private BigDecimal area;
} 