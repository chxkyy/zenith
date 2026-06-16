package com.zenith.admin.service.inst.impl;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 准入评分引擎
 *
 * <p>负责计算准入申请的五维度加权总分。
 * 评分权重配置（硬编码，后续可参数化）：
 * <ul>
 *   <li>公司综合实力：20%</li>
 *   <li>合规与风控：25%</li>
 *   <li>投研能力：25%</li>
 *   <li>服务能力：15%</li>
 *   <li>扣分项：15%（负向计分）</li>
 * </ul></p>
 *
 * <h3>评分规则：</h3>
 * <ul>
 *   <li>正向维度（前4项）：得分 = 原始分数 × 权重 / 100</li>
 *   <li>扣分项：有效得分 = max(0, 100 - 扣分分数) × 权重 / 100</li>
 *   <li>总分 = 各维度加权得分之和</li>
 *   <li>准入分数线：60 分</li>
 * </ul>
 */
@Component
public class ScoringEngine {

    /**
     * 准入分数线
     */
    public static final double PASS_THRESHOLD = 60.0;

    /**
     * 公司综合实力权重：20%
     */
    private static final double WEIGHT_COMPANY_STRENGTH = 20.0;

    /**
     * 合规与风控权重：25%
     */
    private static final double WEIGHT_COMPLIANCE_RISK = 25.0;

    /**
     * 投研能力权重：25%
     */
    private static final double WEIGHT_RESEARCH_ABILITY = 25.0;

    /**
     * 服务能力权重：15%
     */
    private static final double WEIGHT_SERVICE_ABILITY = 15.0;

    /**
     * 扣分项权重：15%
     */
    private static final double WEIGHT_DEDUCTION = 15.0;

    /**
     * 计算加权总分
     *
     * @param companyStrength 公司综合实力（0-100）
     * @param complianceRisk  合规与风控（0-100）
     * @param researchAbility 投研能力（0-100）
     * @param serviceAbility  服务能力（0-100）
     * @param deduction       扣分项（0-100）
     * @return 评分结果
     */
    public ScoreResult calculate(int companyStrength, int complianceRisk,
                                 int researchAbility, int serviceAbility,
                                 int deduction) {
        // 计算各维度加权得分
        Map<String, Double> weightedScores = new LinkedHashMap<>();
        weightedScores.put("companyStrength", companyStrength * WEIGHT_COMPANY_STRENGTH / 100);
        weightedScores.put("complianceRisk", complianceRisk * WEIGHT_COMPLIANCE_RISK / 100);
        weightedScores.put("researchAbility", researchAbility * WEIGHT_RESEARCH_ABILITY / 100);
        weightedScores.put("serviceAbility", serviceAbility * WEIGHT_SERVICE_ABILITY / 100);

        // 扣分项采用负向计分：有效得分 = max(0, 100 - 扣分)
        double effectiveDeductionScore = Math.max(0, 100 - deduction) * WEIGHT_DEDUCTION / 100;
        weightedScores.put("deduction", effectiveDeductionScore);

        // 计算总分
        double totalScore = weightedScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // 保留一位小数
        totalScore = Math.round(totalScore * 10.0) / 10.0;

        return new ScoreResult(weightedScores, totalScore);
    }

    /**
     * 评分结果对象
     */
    @Getter
    public static class ScoreResult {
        /**
         * 各维度加权得分
         */
        private final Map<String, Double> weightedScores;

        /**
         * 总分（保留1位小数）
         */
        private final double totalScore;

        public ScoreResult(Map<String, Double> weightedScores, double totalScore) {
            this.weightedScores = weightedScores;
            this.totalScore = totalScore;
        }
    }
}
