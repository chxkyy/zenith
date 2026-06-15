package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.DataPermissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface DataPermissionMapper extends BaseMapper<DataPermissionDO> {

    /**
     * 查询指定用户在特定数据类型下负责的数据ID列表
     *
     * @param userId   用户ID
     * @param dataType 业务数据类型标识
     * @return 数据ID列表
     */
    List<Long> selectDataIdsByUserAndType(@Param("userId") Long userId,
                                           @Param("dataType") String dataType);

    /**
     * 查询指定业务数据记录的负责人用户ID列表
     *
     * @param dataType 数据类型
     * @param dataId   数据记录ID
     * @return 负责人用户ID列表
     */
    List<Long> selectUserIdsByData(@Param("dataType") String dataType,
                                   @Param("dataId") Long dataId);

    /**
     * 批量查询多个负责人在特定数据类型下负责的数据ID列表
     *
     * @param userIds  用户ID集合
     * @param dataType 数据类型标识
     * @return 数据ID列表（去重）
     */
    Set<Long> selectDataIdsByUsersAndType(@Param("userIds") List<Long> userIds,
                                          @Param("dataType") String dataType);

    /**
     * 检查绑定关系是否存在
     *
     * @param userId   用户ID
     * @param dataType 数据类型
     * @param dataId   数据ID
     * @return 存在返回 true，否则 false
     */
    boolean existsBinding(@Param("userId") Long userId,
                           @Param("dataType") String dataType,
                           @Param("dataId") Long dataId);
}
