package com.agri.insurance.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumLadderRequest {
    @NotNull(message = "最小面积不能为空")
    @Min(value = 0, message = "最小面积不能小于0")
    private BigDecimal minArea;
    
    @NotNull(message = "折扣率不能为空")
    @DecimalMin(value = "0.0", message = "折扣率不能小于0")
    @DecimalMax(value = "1.0", message = "折扣率不能大于1")
    private BigDecimal discountRate;
} 