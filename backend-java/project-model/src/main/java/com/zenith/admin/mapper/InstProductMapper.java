package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.InstProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstProductMapper extends BaseMapper<InstProductDO> {

    /**
     * 查询指定机构的产品列表（可按合作状态筛选）
     */
    List<InstProductDO> selectByInstitutionIdWithStatus(
            @Param("institutionId") Long institutionId,
            @Param("cooperationStatus") String cooperationStatus
    );
}
