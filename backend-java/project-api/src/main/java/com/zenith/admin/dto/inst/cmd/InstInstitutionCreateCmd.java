package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建/编辑机构命令对象
 */
@Data
public class InstInstitutionCreateCmd {

    /**
     * ID（可选，有值则为编辑）
     */
    private Long id;

    @NotBlank(message = "机构全称不能为空")
    private String fullName;

    /**
     * 机构简称
     */
    private String shortName;

    /**
     * 统一社会信用代码（18位格式校验）
     */
    @Pattern(regexp = "^[A-Za-z0-9]{18}$", message = "统一社会信用代码格式不正确")
    private String creditCode;

    /**
     * 机构类型
     */
    private String instType;

    /**
     * 成立日期
     */
    private LocalDate establishDate;

    /**
     * 注册资本
     */
    private String registeredCapital;

    /**
     * 法定代表人
     */
    private String legalRepresentative;

    /**
     * 注册地址
     */
    private String registeredAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * Logo地址
     */
    private String logoUrl;
}
