package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.dto.inst.cmd.InstAdmissionCreateCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 更新准入申请草稿执行器
 *
 * <p>仅允许在 DRAFT 状态下编辑申请信息。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionUpdateCmdExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 执行更新草稿操作
     *
     * @param cmd 更新命令对象（需包含 admissionId）
     * @return 是否更新成功
     */
    public boolean execute(InstAdmissionCreateCmd cmd) {
        if (cmd.getId() == null) {
            throw new BizException("ADMISSION_ID_REQUIRED", "申请单ID不能为空");
        }

        Long currentUserId = UserContext.getUserId();

        // 查询现有记录
        InstAdmissionDO existing = admissionMapper.selectById(cmd.getId());
        if (existing == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 校验状态：仅 DRAFT 状态可编辑
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new BizException("ADMISSION_STATUS_INVALID",
                    "当前状态不允许编辑，当前状态：" + existing.getStatus());
        }

        // 校验权限：仅创建人可编辑
        if (!existing.getCreateUserId().equals(currentUserId)) {
            throw new BizException("NO_PERMISSION", "无权编辑此申请");
        }

        // 更新字段
        existing.setManagerName(cmd.getManagerName());
        existing.setManagerType(cmd.getManagerType());
        existing.setCreditCode(cmd.getCreditCode());
        existing.setRegisteredCapital(cmd.getRegisteredCapital());
        existing.setEstablishDate(cmd.getEstablishDate());
        existing.setLegalRepresentative(cmd.getLegalRepresentative());
        existing.setRegisteredAddress(cmd.getRegisteredAddress());
        existing.setContactPerson(cmd.getContactPerson());
        existing.setContactPhone(cmd.getContactPhone());
        existing.setContactEmail(cmd.getContactEmail());
        existing.setTargetPoolIds(JSON.toJSONString(cmd.getTargetPoolIds()));
        existing.setBasicInfo(JSON.toJSONString(cmd));
        existing.setUpdateUserId(currentUserId);
        existing.setUpdateTime(java.time.LocalDateTime.now());

        return admissionMapper.updateById(existing) > 0;
    }
}
