package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 发起准入申请命令对象
 */
@Data
public class InstAdmissionCreateCmd {

    /**
     * 申请单ID（更新时必填）
     */
    private Long id;

    @NotBlank(message = "管理人名称不能为空")
    private String managerName;

    @NotBlank(message = "管理人类型不能为空")
    private String managerType;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 注册资本
     */
    private String registeredCapital;

    /**
     * 成立日期
     */
    private LocalDate establishDate;

    /**
     * 法定代表人
     */
    private String legalRepresentative;

    /**
     * 注册地址
     */
    private String registeredAddress;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    @NotEmpty(message = "目标机构池不能为空")
    private List<Long> targetPoolIds;
}
