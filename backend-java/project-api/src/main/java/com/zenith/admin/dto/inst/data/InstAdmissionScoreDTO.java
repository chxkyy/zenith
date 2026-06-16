package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评分结果DTO
 */
@Data
public class InstAdmissionScoreDTO extends DTO {

    // ========== 各维度分数 ==========
    /**
     * 公司实力分数（0-100）
     */
    private Integer companyStrength;

    /**
     * 合规风险分数（0-100）
     */
    private Integer complianceRisk;

    /**
     * 研究能力分数（0-100）
     */
    private Integer researchAbility;

    /**
     * 服务能力分数（0-100）
     */
    private Integer serviceAbility;

    /**
     * 扣分项分数（0-100）
     */
    private Integer deduction;

    /**
     * 加权总分
     */
    private Double totalScore;

    /**
     * 评语
     */
    private String comment;

    /**
     * 评分人姓名
     */
    private String scorerName;

    /**
     * 评分时间
     */
    private LocalDateTime scoredTime;
}
