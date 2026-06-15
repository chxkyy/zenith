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
     * 统计多个组织的成员总数（递归统计：包含指定组织及其所有子组织的成员）
     * @param orgIds (List<Long>) - 组织ID列表（包含自身及所有子组织ID）
     * @return 成员总数
     */
    int countMembersByOrgIds(@Param("orgIds") List<Long> orgIds);

    /**
     * 使用 PostgreSQL CTE 递归查询获取指定组织及其所有下级组织的 ID 列表（含自身）
     *
     * <p>相比 Java 递归 getChildOrgIds()，CTE 方式在数据库层面完成树遍历，
     * 性能更好且天然支持环检测（通过 CYCLE 子句）。</p>
     *
     * @param orgId 起始组织ID
     * @return 包含自身及所有下级组织的 ID 列表
     */
    List<Long> selectChildOrgIdsRecursive(@Param("orgId") Long orgId);
}
