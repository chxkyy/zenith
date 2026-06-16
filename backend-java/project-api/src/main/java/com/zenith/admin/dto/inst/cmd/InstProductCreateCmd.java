package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 添加/编辑产品命令对象
 */
@Data
public class InstProductCreateCmd {

    /**
     * ID（可选，有值则为编辑）
     */
    private Long id;

    @NotNull(message = "所属机构ID不能为空")
    private Long institutionId;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 合作状态
     */
    private String cooperationStatus;

    /**
     * 合作开始日期
     */
    private Date cooperationStartDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 联系人
     */
    private String contactPerson;
}
