package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dto.inst.cmd.InstAdmissionSubmitCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 提交评审执行器
 *
 * <p>将申请从 DRAFT 状态推进到 PENDING_REVIEW 状态，
 * 等待评分人员打分。提交后不可再编辑。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionSubmitReviewCmdExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 执行提交评审操作
     *
     * @param cmd 提交命令对象
     */
    public void execute(InstAdmissionSubmitCmd cmd) {
        Long currentUserId = UserContext.getUserId();

        // 查询申请单
        InstAdmissionDO admission = admissionMapper.selectById(cmd.getAdmissionId());
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 校验状态：仅 DRAFT 可提交
        if (!"DRAFT".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许提交，当前状态：" + admission.getStatus());
        }

        // 校验权限：仅创建人可提交
        if (!admission.getCreateUserId().equals(currentUserId)) {
            throw new BizException("NO_PERMISSION", "无权提交此申请");
        }

        // 更新状态为待评分
        admission.setStatus("PENDING_REVIEW");
        admission.setScorerId(null); // 清空评分人，等待分配
        admission.setUpdateUserId(currentUserId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }
}
