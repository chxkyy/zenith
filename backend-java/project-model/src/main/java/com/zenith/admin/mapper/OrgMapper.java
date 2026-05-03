package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.OrgDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgMapper extends BaseMapper<OrgDO> {

    /**
     * 统计指定组织的成员数量（仅统计直接隶属于该组织的用户）
     * @param orgId 组织ID
     * @return 成员数量
     */
    Integer selectCountMemberByOrgId(Long orgId);

    /**
     * 统计多个组织的成员总数（用于递归统计父组织及其所有子组织的成员）
     * @param orgIds 组织ID列表（包含自身及所有子组织ID）
     * @return 成员总数
     */
    int countMembersByOrgIds(@Param("orgIds") List<Long> orgIds);
}
