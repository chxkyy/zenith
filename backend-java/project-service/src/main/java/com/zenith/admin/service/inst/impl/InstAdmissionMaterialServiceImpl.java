package com.zenith.admin.service.inst.impl;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.inst.InstAdmissionMaterialService;
import com.zenith.admin.dto.inst.cmd.InstAdmissionMaterialUploadCmd;
import com.zenith.admin.dto.inst.data.InstAdmissionMaterialDTO;
import com.zenith.admin.service.inst.executor.cmd.InstAdmissionMaterialDeleteCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstAdmissionMaterialUploadCmdExe;
import com.zenith.admin.service.inst.executor.qry.InstAdmissionMaterialListQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 准入申请材料管理服务实现类
 *
 * <p>负责准入申请过程中附件材料的管理，包括上传、查询和删除操作。</p>
 */
@Service
@RequiredArgsConstructor
public class InstAdmissionMaterialServiceImpl implements InstAdmissionMaterialService {

    private final InstAdmissionMaterialUploadCmdExe uploadCmdExe;
    private final InstAdmissionMaterialDeleteCmdExe deleteCmdExe;
    private final InstAdmissionMaterialListQryExe listQryExe;
    private final com.zenith.admin.mapper.inst.InstAdmissionMaterialMapper materialMapper;

    @Override
    public SingleResponse<Long> uploadMaterial(InstAdmissionMaterialUploadCmd cmd) {
        Long materialId = uploadCmdExe.execute(cmd);
        return SingleResponse.of(materialId);
    }

    @Override
    public List<InstAdmissionMaterialDTO> listByAdmissionId(Long admissionId) {
        return listQryExe.execute(admissionId);
    }

    @Override
    public SingleResponse<Boolean> deleteMaterial(Long materialId) {
        boolean result = deleteCmdExe.execute(materialId);
        return SingleResponse.of(result);
    }

    @Override
    public void deleteByAdmissionId(Long admissionId) {
        materialMapper.deleteByAdmissionId(admissionId);
    }
}
