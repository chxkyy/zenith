package com.zenith.admin.service.inst.executor.qry;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zenith.admin.dto.inst.data.InstAdmissionDTO;
import com.zenith.admin.dto.inst.qry.InstAdmissionPageQuery;
import com.zenith.admin.dataobject.inst.InstAdmissionDO;
import com.zenith.admin.mapper.inst.InstAdmissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 待审批列表查询执行器
 *
 * <p>返回所有处于 PENDING_APPROVAL 状态的申请，按创建时间升序排列，
 * 便于审批人员优先处理较早的申请。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionPendingApprovalQryExe {

    private final InstAdmissionMapper admissionMapper;

    /**
     * 查询待审批列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    public Page<InstAdmissionDTO> execute(InstAdmissionPageQuery query) {
        // 构建分页对象
        Page<InstAdmissionDO> page = new Page<>(query.getPageIndex(), query.getPageSize());

        // 执行分页查询
        Page<InstAdmissionDO> resultPage = admissionMapper.selectPendingApproval(page);

        // 转换为 DTO 列表
        List<InstAdmissionDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建返回的分页结果
        Page<InstAdmissionDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 将 DO 转换为 DTO
     */
    private InstAdmissionDTO convertToDTO(InstAdmissionDO doObj) {
        InstAdmissionDTO dto = new InstAdmissionDTO();
        dto.setId(doObj.getId());
        dto.setAdmissionNo(doObj.getAdmissionNo());
        dto.setManagerName(doObj.getManagerName());
        dto.setManagerType(doObj.getManagerType());
        dto.setStatus(doObj.getStatus());
        dto.setScorerId(doObj.getScorerId());
        dto.setApproverId(doObj.getApproverId());
        dto.setCreateUserId(doObj.getCreateUserId());
        dto.setCreatedTime(doObj.getCreatedTime());
        dto.setUpdateTime(doObj.getUpdateTime());

        // 解析目标池 ID 列表
        if (doObj.getTargetPoolIds() != null) {
            dto.setTargetPoolIds(JSON.parseArray(doObj.getTargetPoolIds(), Long.class));
        }

        return dto;
    }
}
