package com.zenith.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.TaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<TaskDO> {

    List<TaskDO> selectByProcessInstanceId(Long processInstanceId);

    List<TaskDO> selectByProcessInstanceAndNode(@Param("processInstanceId") Long processInstanceId, @Param("nodeOrder") Integer nodeOrder);

    int updateStatusWithVersion(@Param("id") Long id, @Param("status") Integer status, @Param("version") Integer version);

    List<TaskDO> selectPendingByAssignee(@Param("assigneeId") Long assigneeId);

    List<TaskDO> selectDoneByAssignee(@Param("assigneeId") Long assigneeId);
}
