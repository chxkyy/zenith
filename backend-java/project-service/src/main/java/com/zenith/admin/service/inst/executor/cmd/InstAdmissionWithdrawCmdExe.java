package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 撤销申请执行器
 *
 * <p>允许申请人在非终态下撤销自己的申请。
 * 终态包括：APPROVED、REJECTED</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionWithdrawCmdExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 执行撤销操作
     *
     * @param admissionId 申请单ID
     */
    public void execute(Long admissionId) {
        Long currentUserId = UserContext.getUserId();

        // 查询申请单
        InstAdmissionDO admission = admissionMapper.selectById(admissionId);
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 校验是否为终态
        if ("APPROVED".equals(admission.getStatus()) || "REJECTED".equals(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_FINAL",
                    "当前状态不允许撤销，当前状态：" + admission.getStatus());
        }

        // 校验权限：仅创建人可撤销
        if (!admission.getCreateUserId().equals(currentUserId)) {
            throw new BizException("NO_PERMISSION", "无权撤销此申请");
        }

        // 更新状态为已撤销
        admission.setStatus("WITHDRAWN");
        admission.setUpdateUserId(currentUserId);
        admission.setUpdateTime(LocalDateTime.now());

        admissionMapper.updateById(admission);
    }
}
