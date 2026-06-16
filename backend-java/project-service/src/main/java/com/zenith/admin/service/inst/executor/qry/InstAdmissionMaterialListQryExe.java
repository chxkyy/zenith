package com.zenith.admin.service.inst.executor.qry;

import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dataobject.inst.InstAdmissionMaterialDO;
import com.zenith.admin.dto.inst.data.InstAdmissionMaterialDTO;
import com.zenith.admin.mapper.FileMapper;
import com.zenith.admin.mapper.inst.InstAdmissionMaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 材料列表查询执行器
 *
 * <p>查询指定申请的所有材料记录，包含文件详细信息。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionMaterialListQryExe {

    private final InstAdmissionMaterialMapper materialMapper;
    private final FileMapper fileMapper;

    /**
     * 查询指定申请的材料列表
     *
     * @param admissionId 申请单ID
     * @return 材料列表（按排序顺序）
     */
    public List<InstAdmissionMaterialDTO> execute(Long admissionId) {
        List<InstAdmissionMaterialDO> materials = materialMapper.selectByAdmissionId(admissionId);

        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将 DO 转换为 DTO
     */
    private InstAdmissionMaterialDTO convertToDTO(InstAdmissionMaterialDO doObj) {
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
}
