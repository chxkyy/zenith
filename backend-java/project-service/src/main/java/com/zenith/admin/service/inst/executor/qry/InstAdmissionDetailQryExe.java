package com.zenith.admin.service.inst.executor.qry;

import com.alibaba.cola.exception.BizException;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.dto.inst.data.InstAdmissionDTO;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.dataobject.inst.InstAdmissionLogDO;
import com.zenith.admin.dataobject.inst.InstAdmissionMaterialDO;
import com.zenith.admin.dto.inst.data.InstAdmissionLogDTO;
import com.zenith.admin.dto.inst.data.InstAdmissionMaterialDTO;
import com.zenith.admin.mapper.FileMapper;
import com.zenith.admin.mapper.inst.InstAdmissionLogMapper;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import com.zenith.admin.mapper.inst.InstAdmissionMaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请详情查询执行器
 *
 * <p>查询准入申请的完整详情，包括基本信息、材料列表和操作日志。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionDetailQryExe {

    private final InstAdmissionMapper admissionMapper;
    private final InstAdmissionMaterialMapper materialMapper;
    private final InstAdmissionLogMapper logMapper;
    private final FileMapper fileMapper;

    /**
     * 查询申请详情
     *
     * @param id 申请单ID
     * @return 申请详情 DTO（含材料和日志）
     */
    public InstAdmissionDTO execute(Long id) {
        // 查询申请单
        InstAdmissionDO admission = admissionMapper.selectById(id);
        if (admission == null) {
            throw new BizException("ADMISSION_NOT_FOUND", "准入申请不存在");
        }

        // 转换为 DTO
        InstAdmissionDTO dto = convertToDTO(admission);

        // 查询并填充材料列表
        List<InstAdmissionMaterialDO> materials = materialMapper.selectByAdmissionId(id);
        dto.setMaterials(materials.stream()
                .map(this::convertMaterialToDTO)
                .collect(Collectors.toList()));

        // 查询并填充操作日志
        List<InstAdmissionLogDO> logs = logMapper.selectByAdmissionIdOrderByTime(id);
        dto.setLogs(logs.stream()
                .map(this::convertLogToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * 将 DO 转换为 DTO
     */
    private InstAdmissionDTO convertToDTO(InstAdmissionDO doObj) {
        InstAdmissionDTO dto = new InstAdmissionDTO();
        dto.setId(doObj.getId());
        dto.setAdmissionNo(doObj.getAdmissionNo());
        dto.setProcessInstanceId(doObj.getProcessInstanceId());
        dto.setManagerName(doObj.getManagerName());
        dto.setManagerType(doObj.getManagerType());
        dto.setCreditCode(doObj.getCreditCode());
        dto.setRegisteredCapital(doObj.getRegisteredCapital());
        dto.setEstablishDate(doObj.getEstablishDate());
        dto.setLegalRepresentative(doObj.getLegalRepresentative());
        dto.setRegisteredAddress(doObj.getRegisteredAddress());
        dto.setContactPerson(doObj.getContactPerson());
        dto.setContactPhone(doObj.getContactPhone());
        dto.setContactEmail(doObj.getContactEmail());

        // 解析目标池 ID 列表
        if (doObj.getTargetPoolIds() != null) {
            dto.setTargetPoolIds(JSON.parseArray(doObj.getTargetPoolIds(), Long.class));
        }

        dto.setStatus(doObj.getStatus());
        dto.setScorerId(doObj.getScorerId());
        dto.setApproverId(doObj.getApproverId());
        dto.setApprovalOpinion(doObj.getApprovalOpinion());
        dto.setRejectionReason(doObj.getRejectionReason());
        dto.setCreateUserId(doObj.getCreateUserId());
        dto.setCreatedTime(doObj.getCreatedTime());
        dto.setUpdateUserId(doObj.getUpdateUserId());
        dto.setUpdateTime(doObj.getUpdateTime());

        return dto;
    }

    /**
     * 将材料 DO 转换为 DTO（含文件信息）
     */
    private InstAdmissionMaterialDTO convertMaterialToDTO(InstAdmissionMaterialDO doObj) {
        InstAdmissionMaterialDTO dto =
                new InstAdmissionMaterialDTO();
        dto.setId(doObj.getId());
        dto.setAdmissionId(doObj.getAdmissionId());
        dto.setMaterialCategory(doObj.getMaterialCategory());
        dto.setMaterialName(doObj.getMaterialName());
        dto.setFileId(doObj.getFileId());
        dto.setSortOrder(doObj.getSortOrder());
        dto.setCreatedTime(doObj.getCreatedTime());

        // 查询文件信息
        if (doObj.getFileId() != null) {
            FileDO file = fileMapper.selectById(doObj.getFileId());
            if (file != null) {
                dto.setFileName(file.getOriginalName());
                dto.setFileUrl(file.getPath());
                dto.setFileSize(file.getSize());
            }
        }

        return dto;
    }

    /**
     * 将日志 DO 转换为 DTO
     */
    private InstAdmissionLogDTO convertLogToDTO(InstAdmissionLogDO doObj) {
        InstAdmissionLogDTO dto =
                new InstAdmissionLogDTO();
        dto.setId(doObj.getId());
        dto.setAdmissionId(doObj.getAdmissionId());
        dto.setAction(doObj.getAction());
        dto.setOperatorId(doObj.getOperatorId());
        dto.setOperatorName(doObj.getOperatorName());
        dto.setDetail(doObj.getDetail());
        dto.setCreatedTime(doObj.getCreatedTime());
        return dto;
    }
}
