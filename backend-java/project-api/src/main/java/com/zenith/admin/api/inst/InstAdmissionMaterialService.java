package com.zenith.admin.api.inst;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.inst.cmd.InstAdmissionMaterialUploadCmd;
import com.zenith.admin.dto.inst.data.InstAdmissionMaterialDTO;

import java.util.List;

/**
 * 准入申请材料管理服务接口
 *
 * <p>负责准入申请过程中附件材料的管理，包括上传、查询和删除操作。</p>
 */
public interface InstAdmissionMaterialService {

    /**
     * 上传材料
     *
     * <p>将文件关联到指定的准入申请单。校验：
     * <ul>
     *   <li>申请单必须存在且非终态</li>
     *   <li>文件必须在文件系统中存在</li>
     * </ul></p>
     *
     * @param cmd 材料上传命令对象
     * @return 材料记录ID
     */
    SingleResponse<Long> uploadMaterial(InstAdmissionMaterialUploadCmd cmd);

    /**
     * 查询指定申请的材料列表
     *
     * @param admissionId 申请单ID
     * @return 材料列表（按排序顺序）
     */
    List<InstAdmissionMaterialDTO> listByAdmissionId(Long admissionId);

    /**
     * 删除指定材料
     *
     * @param materialId 材料记录ID
     * @return 是否删除成功
     */
    SingleResponse<Boolean> deleteMaterial(Long materialId);

    /**
     * 批量删除某申请的所有材料
     *
     * <p>通常在删除申请单时调用，级联清理关联材料。</p>
     *
     * @param admissionId 申请单ID
     */
    void deleteByAdmissionId(Long admissionId);
}
