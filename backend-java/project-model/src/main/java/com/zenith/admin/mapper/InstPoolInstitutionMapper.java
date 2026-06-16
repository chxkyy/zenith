package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstPoolInstitutionMapper extends BaseMapper<InstPoolInstitutionDO> {

    List<InstPoolInstitutionDO> selectByPoolId(@Param("poolId") Long poolId);

    List<InstPoolInstitutionDO> selectByInstitutionId(@Param("institutionId") Long institutionId);

    Integer countByPoolId(@Param("poolId") Long poolId);
}
