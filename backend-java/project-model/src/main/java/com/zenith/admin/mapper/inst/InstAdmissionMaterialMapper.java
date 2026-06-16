package com.zenith.admin.mapper.inst;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.inst.InstAdmissionMaterialDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstAdmissionMaterialMapper extends BaseMapper<InstAdmissionMaterialDO> {

    /**
     * 查询指定申请的所有材料（按排序顺序）
     *
     * @param admissionId 准入申请ID
     * @return 材料列表
     */
    List<InstAdmissionMaterialDO> selectByAdmissionId(@Param("admissionId") Long admissionId);

    /**
     * 删除指定申请的所有材料
     *
     * @param admissionId 准入申请ID
     * @return 删除的记录数
     */
    int deleteByAdmissionId(@Param("admissionId") Long admissionId);
}
