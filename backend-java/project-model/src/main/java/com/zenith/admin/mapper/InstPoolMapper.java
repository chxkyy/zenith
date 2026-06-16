package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zenith.admin.dataobject.InstPoolDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InstPoolMapper extends BaseMapper<InstPoolDO> {

    /**
     * 分页查询机构池（按名称、类型、状态筛选）
     */
    Page<InstPoolDO> selectPageByNameTypeStatus(
            Page<InstPoolDO> page,
            @Param("name") String name,
            @Param("poolType") String poolType,
            @Param("status") Integer status
    );
}
