package com.agri.insurance.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class InsuranceOrderRequest {
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "投保面积不能为空")
    @DecimalMin(value = "0.01", message = "投保面积必须大于0")
    private BigDecimal area;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String contactPhone;
} 