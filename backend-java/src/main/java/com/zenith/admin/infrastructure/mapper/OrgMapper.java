package com.zenith.admin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgMapper extends BaseMapper<OrgDO> {
}
