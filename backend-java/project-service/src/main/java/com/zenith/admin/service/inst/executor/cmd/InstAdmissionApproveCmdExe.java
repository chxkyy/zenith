package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dto.inst.cmd.InstAdmissionApproveCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审批操作执行器
 *
 * <p>根据 action 字段分发到不同的审批处理逻辑：
 * <ul>
 *   <li>approve：审批通过</li>
 *   <li>reject：审批驳回</li>
 *   <li>return：退回补正</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionApproveCmdExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 执行审批操作
     *
     * @param cmd 审批命令对象
     */
    public void execute(InstAdmissionApproveCmd cmd) {
        Long currentUserId = UserContext.getUserId();

        // 查询申请单
        InstAdmissionDO admission = admissionMapper.selectById(cmd.getAdmissionId());
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 根据 action 分发处理
        switch (cmd.getAction().toLowerCase()) {
            case "approve":
                handleApprove(admission, currentUserId, cmd.getOpinion());
                break;
            case "reject":
                handleReject(admission, currentUserId, cmd.getOpinion());
                break;
            case "return":
                handleReturn(admission, currentUserId, cmd.getOpinion());
                break;
            default:
                throw new BizException("INVALID_ACTION", "无效的审批操作：" + cmd.getAction());
        }
    }

    /**
     * 处理审批通过
     *
     * <p>状态变更为 APPROVED</p>
     */
    private void handleApprove(InstAdmissionDO admission, Long approverId, String opinion) {
        // 校验状态：仅 PENDING_APPROVAL 可通过
        if (!"PENDING_APPROVAL".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许审批通过，当前状态：" + admission.getStatus());
        }

        admission.setStatus("APPROVED");
        admission.setApproverId(approverId);
        admission.setApprovalOpinion(opinion);
        admission.setRejectionReason(null);
        admission.setUpdateUserId(approverId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }

    /**
     * 处理审批驳回
     *
     * <p>状态变更为 REJECTED</p>
     */
    private void handleReject(InstAdmissionDO admission, Long approverId, String opinion) {
        // 校验状态：仅 PENDING_APPROVAL 可驳回
        if (!"PENDING_APPROVAL".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许驳回，当前状态：" + admission.getStatus());
        }

        // 驳回时意见必填
        if (opinion == null || opinion.trim().length() < 10) {
            throw new BizException("OPINION_REQUIRED", "驳回意见不能少于10个字符");
        }

        admission.setStatus("REJECTED");
        admission.setApproverId(approverId);
        admission.setApprovalOpinion(opinion);
        admission.setRejectionReason(opinion);
        admission.setUpdateUserId(approverId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }

    /**
     * 处理退回补正
     *
     * <p>状态变更为 DRAFT，申请人可重新编辑后再次提交</p>
     */
    private void handleReturn(InstAdmissionDO admission, Long operatorId, String reason) {
        // 校验状态：PENDING_REVIEW 或 PENDING_APPROVAL 可退回
        if (!"PENDING_REVIEW".equals(admission.getStatus()) && !"PENDING_APPROVAL".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许退回，当前状态：" + admission.getStatus());
        }

        admission.setStatus("DRAFT");
        admission.setScorerId(null); // 清空评分人
        admission.setScoreResult(null); // 清空评分结果
        admission.setApproverId(null); // 清空审批人
        admission.setApprovalOpinion(reason);
        admission.setUpdateUserId(operatorId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }
}
