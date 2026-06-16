package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dto.inst.cmd.InstAdmissionMaterialUploadCmd;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.dataobject.inst.InstAdmissionMaterialDO;
import com.zenith.admin.mapper.FileMapper;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import com.zenith.admin.mapper.inst.InstAdmissionMaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 材料上传执行器
 *
 * <p>将文件关联到准入申请单，校验申请单状态和文件存在性。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionMaterialUploadCmdExe {

    private final InstAdmissionMapper admissionMapper;
    private final InstAdmissionMaterialMapper materialMapper;
    private final FileMapper fileMapper;

    /**
     * 执行材料上传操作
     *
     * @param cmd 材料上传命令对象
     * @return 材料记录ID
     */
    public Long execute(InstAdmissionMaterialUploadCmd cmd) {
        Long currentUserId = UserContext.getUserId();

        // 校验申请单存在且非终态
        InstAdmissionDO admission = admissionMapper.selectById(cmd.getAdmissionId());
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 终态不允许上传材料
        if (isFinalStatus(admission.getStatus())) {
            throw new BizException("ADMISSION_STATUS_FINAL",
                    "当前状态不允许上传材料，当前状态：" + admission.getStatus());
        }

        // 校验文件存在
        FileDO file = fileMapper.selectById(cmd.getFileId());
        if (file == null) {
            throw new BizException("FILE_NOT_FOUND", "文件不存在");
        }

        // 查询当前最大排序号
        Integer maxSortOrder = materialMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InstAdmissionMaterialDO>()
                        .eq(InstAdmissionMaterialDO::getAdmissionId, cmd.getAdmissionId())
                        .orderByDesc(InstAdmissionMaterialDO::getSortOrder)
                        .last("LIMIT 1")
        ).stream()
                .map(InstAdmissionMaterialDO::getSortOrder)
                .findFirst()
                .orElse(0);

        // 构建材料记录
        InstAdmissionMaterialDO material = new InstAdmissionMaterialDO();
        material.setAdmissionId(cmd.getAdmissionId());
        material.setMaterialCategory(cmd.getMaterialCategory());
        material.setMaterialName(cmd.getMaterialName() != null ? cmd.getMaterialName() : file.getOriginalName());
        material.setFileId(cmd.getFileId());
        material.setSortOrder(maxSortOrder + 1);
        material.setCreateUserId(currentUserId);
        material.setCreatedTime(LocalDateTime.now());

        materialMapper.insert(material);

        return material.getId();
    }

    /**
     * 判断是否为终态
     *
     * @param status 申请状态
     * @return 是否为终态
     */
    private boolean isFinalStatus(String status) {
        return "APPROVED".equals(status) || "REJECTED".equals(status) || "WITHDRAWN".equals(status);
    }
}
