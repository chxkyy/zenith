package com.zenith.admin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.infrastructure.dataobject.OperLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperLogMapper extends BaseMapper<OperLogDO> {
}
