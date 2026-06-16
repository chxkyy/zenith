package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评分提交命令对象
 */
@Data
public class InstAdmissionScoreCmd {

    @NotNull(message = "申请单ID不能为空")
    private Long admissionId;

    /**
     * 公司实力（0-100）
     */
    @Min(value = 0, message = "公司实力分数不能小于0")
    @Max(value = 100, message = "公司实力分数不能大于100")
    private Integer companyStrength;

    /**
     * 合规风险（0-100）
     */
    @Min(value = 0, message = "合规风险分数不能小于0")
    @Max(value = 100, message = "合规风险分数不能大于100")
    private Integer complianceRisk;

    /**
     * 研究能力（0-100）
     */
    @Min(value = 0, message = "研究能力分数不能小于0")
    @Max(value = 100, message = "研究能力分数不能大于100")
    private Integer researchAbility;

    /**
     * 服务能力（0-100）
     */
    @Min(value = 0, message = "服务能力分数不能小于0")
    @Max(value = 100, message = "服务能力分数不能大于100")
    private Integer serviceAbility;

    /**
     * 扣分项（0-100）
     */
    @Min(value = 0, message = "扣分项分数不能小于0")
    @Max(value = 100, message = "扣分项分数不能大于100")
    private Integer deduction;

    /**
     * 评语（选填）
     */
    @Size(max = 500, message = "评语长度不能超过500字")
    private String comment;
}
