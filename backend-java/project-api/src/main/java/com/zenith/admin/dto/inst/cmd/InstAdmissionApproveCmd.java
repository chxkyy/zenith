package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批操作命令对象
 */
@Data
public class InstAdmissionApproveCmd {

    @NotNull(message = "申请单ID不能为空")
    private Long admissionId;

    @NotBlank(message = "审批操作不能为空")
    private String action; // approve=通过 reject=驳回 return=退回

    /**
     * 审批意见（驳回时必填且不少于10个字符）
     */
    @Size(min = 10, message = "驳回意见不能少于10个字符")
    private String opinion;
}
