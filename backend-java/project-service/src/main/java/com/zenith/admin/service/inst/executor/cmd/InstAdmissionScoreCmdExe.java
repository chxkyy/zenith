package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.dto.inst.cmd.InstAdmissionScoreCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import com.zenith.admin.service.inst.impl.ScoringEngine;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 评分执行器
 *
 * <p>对准入申请进行五维度加权评分：
 * <ul>
 *   <li>公司综合实力：20%</li>
 *   <li>合规与风控：25%</li>
 *   <li>投研能力：25%</li>
 *   <li>服务能力：15%</li>
 *   <li>扣分项：15%（负向计分）</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionScoreCmdExe {

    private static final Logger log = LoggerFactory.getLogger(InstAdmissionScoreCmdExe.class);

    private final InstAdmissionMapper admissionMapper;
    private final ScoringEngine scoringEngine;

    /**
     * 执行评分操作
     *
     * @param cmd 评分命令对象
     */
    public void execute(InstAdmissionScoreCmd cmd) {
        Long currentUserId = UserContext.getUserId();

        // 查询申请单
        InstAdmissionDO admission = admissionMapper.selectById(cmd.getAdmissionId());
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 校验状态：仅 PENDING_REVIEW 可评分
        if (!"PENDING_REVIEW".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许评分，当前状态：" + admission.getStatus());
        }

        // 计算加权总分
        ScoringEngine.ScoreResult scoreResult = scoringEngine.calculate(
                cmd.getCompanyStrength(),
                cmd.getComplianceRisk(),
                cmd.getResearchAbility(),
                cmd.getServiceAbility(),
                cmd.getDeduction()
        );

        // 检查是否低于准入线
        if (scoreResult.getTotalScore() < ScoringEngine.PASS_THRESHOLD) {
            log.warn("申请单[{}]总分为{}，低于准入线{}", cmd.getAdmissionId(), scoreResult.getTotalScore(), ScoringEngine.PASS_THRESHOLD);
        }

        // 构建评分结果 JSON
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("companyStrength", cmd.getCompanyStrength());
        resultMap.put("complianceRisk", cmd.getComplianceRisk());
        resultMap.put("researchAbility", cmd.getResearchAbility());
        resultMap.put("serviceAbility", cmd.getServiceAbility());
        resultMap.put("deduction", cmd.getDeduction());
        resultMap.put("weightedScores", scoreResult.getWeightedScores());
        resultMap.put("totalScore", scoreResult.getTotalScore());
        resultMap.put("passed", scoreResult.getTotalScore() >= ScoringEngine.PASS_THRESHOLD);
        resultMap.put("comment", cmd.getComment() != null ? cmd.getComment() : "");

        // 更新申请单
        admission.setStatus("PENDING_APPROVAL");
        admission.setScorerId(currentUserId);
        admission.setScoreResult(JSON.toJSONString(resultMap));
        admission.setUpdateUserId(currentUserId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }
}
