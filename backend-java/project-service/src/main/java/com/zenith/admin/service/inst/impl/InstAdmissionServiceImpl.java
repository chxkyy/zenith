package com.zenith.admin.service.inst.impl;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.inst.InstAdmissionLogService;
import com.zenith.admin.api.inst.InstAdmissionService;
import com.zenith.admin.dto.inst.cmd.InstAdmissionApproveCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionScoreCmd;
import com.zenith.admin.dto.inst.cmd.InstAdmissionSubmitCmd;
import com.zenith.admin.dto.inst.data.InstAdmissionDTO;
import com.zenith.admin.dto.inst.qry.InstAdmissionPageQuery;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.service.inst.executor.cmd.*;
import com.zenith.admin.service.inst.executor.qry.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 准入申请核心服务实现类
 *
 * <p>实现准入申请的全流程管理，包括草稿创建、提交评审、评分、审批等操作。
 * 通过组合各个 Executor 完成具体业务逻辑。</p>
 */
@Service
@RequiredArgsConstructor
public class InstAdmissionServiceImpl implements InstAdmissionService {

    private final InstAdmissionCreateCmdExe createCmdExe;
    private final InstAdmissionUpdateCmdExe updateCmdExe;
    private final InstAdmissionSubmitReviewCmdExe submitReviewCmdExe;
    private final InstAdmissionScoreCmdExe scoreCmdExe;
    private final InstAdmissionApproveCmdExe approveCmdExe;
    private final InstAdmissionWithdrawCmdExe withdrawCmdExe;
    private final InstAdmissionDetailQryExe detailQryExe;
    private final InstAdmissionMyApplicationsQryExe myApplicationsQryExe;
    private final InstAdmissionPendingReviewQryExe pendingReviewQryExe;
    private final InstAdmissionPendingApprovalQryExe pendingApprovalQryExe;
    private final InstAdmissionLogService logService;

    @Override
    public SingleResponse<Long> createDraft(InstAdmissionCreateCmd cmd) {
        Long admissionId = createCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(admissionId, "CREATE", userId, "系统用户", "创建准入申请草稿");

        return SingleResponse.of(admissionId);
    }

    @Override
    public SingleResponse<Boolean> updateDraft(InstAdmissionCreateCmd cmd) {
        boolean result = updateCmdExe.execute(cmd);

        if (result && cmd.getId() != null) {
            // 记录操作日志
            Long userId = UserContext.getUserId();
            logService.append(cmd.getId(), "UPDATE", userId, "系统用户", "更新准入申请草稿");
        }

        return SingleResponse.of(result);
    }

    @Override
    public SingleResponse<Void> submitForReview(InstAdmissionSubmitCmd cmd) {
        submitReviewCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(cmd.getAdmissionId(), "SUBMIT", userId, "系统用户", "提交评审");

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<Void> score(InstAdmissionScoreCmd cmd) {
        scoreCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(cmd.getAdmissionId(), "SCORE", userId, "系统用户", "完成评分");
        logService.append(cmd.getAdmissionId(), "SUBMIT_APPROVE", userId, "系统用户", "提交审批");

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<Void> returnForRevision(Long admissionId, String reason) {
        // 构造退回命令
        InstAdmissionApproveCmd cmd = new InstAdmissionApproveCmd();
        cmd.setAdmissionId(admissionId);
        cmd.setAction("return");
        cmd.setOpinion(reason);

        approveCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(admissionId, "RETURN", userId, "系统用户", reason);

        return SingleResponse.buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<Void> approve(InstAdmissionApproveCmd cmd) {
        approveCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(cmd.getAdmissionId(), "APPROVE", userId, "系统用户",
                cmd.getOpinion() != null ? cmd.getOpinion() : "审批通过");

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<Void> reject(InstAdmissionApproveCmd cmd) {
        cmd.setAction("reject");
        approveCmdExe.execute(cmd);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(cmd.getAdmissionId(), "REJECT", userId, "系统用户",
                cmd.getOpinion() != null ? cmd.getOpinion() : "审批驳回");

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<Void> withdraw(Long admissionId) {
        withdrawCmdExe.execute(admissionId);

        // 记录操作日志
        Long userId = UserContext.getUserId();
        logService.append(admissionId, "WITHDRAW", userId, "系统用户", "撤销申请");

        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<InstAdmissionDTO> detail(Long id) {
        InstAdmissionDTO dto = detailQryExe.execute(id);
        return SingleResponse.of(dto);
    }

    @Override
    public PageResponse<InstAdmissionDTO> myApplications(InstAdmissionPageQuery query) {
        Page<InstAdmissionDTO> pageResult = myApplicationsQryExe.execute(query);
        return PageResponse.of(pageResult.getRecords(), (int) pageResult.getTotal(),
                (int) pageResult.getSize(), (int) pageResult.getCurrent());
    }

    @Override
    public PageResponse<InstAdmissionDTO> pendingReview(InstAdmissionPageQuery query) {
        Page<InstAdmissionDTO> pageResult = pendingReviewQryExe.execute(query);
        return PageResponse.of(pageResult.getRecords(), (int) pageResult.getTotal(),
                (int) pageResult.getSize(), (int) pageResult.getCurrent());
    }

    @Override
    public PageResponse<InstAdmissionDTO> pendingApproval(InstAdmissionPageQuery query) {
        Page<InstAdmissionDTO> pageResult = pendingApprovalQryExe.execute(query);
        return PageResponse.of(pageResult.getRecords(), (int) pageResult.getTotal(),
                (int) pageResult.getSize(), (int) pageResult.getCurrent());
    }
}
