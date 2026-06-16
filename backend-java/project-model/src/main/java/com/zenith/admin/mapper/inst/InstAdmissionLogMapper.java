package com.zenith.admin.mapper.inst;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zenith.admin.dataobject.inst.InstAdmissionLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstAdmissionLogMapper extends BaseMapper<InstAdmissionLogDO> {

    /**
     * 查询指定申请的操作日志（按时间倒序排列）
     *
     * @param admissionId 准入申请ID
     * @return 日志列表（按创建时间倒序）
     */
    List<InstAdmissionLogDO> selectByAdmissionIdOrderByTime(@Param("admissionId") Long admissionId);
}
