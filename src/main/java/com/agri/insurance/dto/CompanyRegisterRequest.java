package com.agri.insurance.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CompanyRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "公司名称不能为空")
    private String companyName;

    @NotBlank(message = "统一社会信用代码不能为空")
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$", message = "请输入正确的统一社会信用代码")
    private String creditCode;

    @NotBlank(message = "公司类型不能为空")
    @Pattern(regexp = "^(headquarters|branch)$", message = "公司类型必须是 headquarters 或 branch")
    private String type;

    private String parentCompany; // 分公司必填

    @NotBlank(message = "城市代码不能为空")
    private String cityCode;

    @NotBlank(message = "区县代码不能为空")
    private String districtCode;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;

    private String householdDoc; // 营业执照等资质文件URL
} 