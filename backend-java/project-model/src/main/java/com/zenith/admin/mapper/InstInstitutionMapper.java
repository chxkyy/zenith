package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zenith.admin.dataobject.InstInstitutionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstInstitutionMapper extends BaseMapper<InstInstitutionDO> {

    /**
     * 按关键词搜索机构（名称或信用代码模糊匹配）
     *
     * @param keyword 搜索关键词（匹配全称、简称或信用代码）
     * @return 机构列表
     */
    List<InstInstitutionDO> selectByKeyword(@Param("keyword") String keyword);

    /**
     * 分页查询机构列表（按关键词筛选）
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    Page<InstInstitutionDO> selectPageByKeyword(
            Page<InstInstitutionDO> page,
            @Param("keyword") String keyword
    );
}
