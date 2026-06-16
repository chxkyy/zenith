package com.zenith.admin.mapper.inst;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InstAdmissionMapper extends BaseMapper<InstAdmissionDO> {

    /**
     * 查询我的准入申请列表
     *
     * @param page 分页参数
     * @param createUserId 创建人ID
     * @return 分页结果
     */
    Page<InstAdmissionDO> selectMyApplications(
            Page<InstAdmissionDO> page,
            @Param("createUserId") Long createUserId
    );

    /**
     * 按状态查询准入申请
     *
     * @param page 分页参数
     * @param status 申请状态
     * @return 分页结果
     */
    Page<InstAdmissionDO> selectByStatus(
            Page<InstAdmissionDO> page,
            @Param("status") String status
    );

    /**
     * 查询待评审的申请
     *
     * @param page 分页参数
     * @return 分页结果
     */
    Page<InstAdmissionDO> selectPendingReview(Page<InstAdmissionDO> page);

    /**
     * 查询待审批的申请
     *
     * @param page 分页参数
     * @return 分页结果
     */
    Page<InstAdmissionDO> selectPendingApproval(Page<InstAdmissionDO> page);
}
