package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.inst.InstAdmissionMaterialDO;
import com.zenith.admin.mapper.inst.InstAdmissionMaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 材料删除执行器
 *
 * <p>删除指定的申请材料记录。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionMaterialDeleteCmdExe {

    private final InstAdmissionMaterialMapper materialMapper;

    /**
     * 执行删除操作
     *
     * @param materialId 材料记录ID
     * @return 是否删除成功
     */
    public boolean execute(Long materialId) {
        // 查询材料记录
        InstAdmissionMaterialDO material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BizException("MATERIAL_NOT_FOUND", "材料记录不存在");
        }

        return materialMapper.deleteById(materialId) > 0;
    }
}
