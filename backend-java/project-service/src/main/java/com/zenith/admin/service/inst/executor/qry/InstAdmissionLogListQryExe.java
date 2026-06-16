package com.zenith.admin.service.inst.executor.qry;

import com.zenith.admin.dataobject.inst.InstAdmissionLogDO;
import com.zenith.admin.dto.inst.data.InstAdmissionLogDTO;
import com.zenith.admin.mapper.inst.InstAdmissionLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志列表查询执行器
 *
 * <p>查询指定申请的所有操作日志，按时间倒序排列。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionLogListQryExe {

    private final InstAdmissionLogMapper logMapper;

    /**
     * 查询指定申请的操作日志
     *
     * @param admissionId 申请单ID
     * @return 日志列表（按时间倒序）
     */
    public List<InstAdmissionLogDTO> execute(Long admissionId) {
        List<InstAdmissionLogDO> logs = logMapper.selectByAdmissionIdOrderByTime(admissionId);

        return logs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将 DO 转换为 DTO
     */
    private InstAdmissionLogDTO convertToDTO(InstAdmissionLogDO doObj) {
        InstAdmissionLogDTO dto =
                new InstAdmissionLogDTO();
        dto.setId(doObj.getId());
        dto.setAdmissionId(doObj.getAdmissionId());
        dto.setAction(doObj.getAction());
        dto.setOperatorId(doObj.getOperatorId());
        dto.setOperatorName(doObj.getOperatorName());
        dto.setDetail(doObj.getDetail());
        dto.setCreatedTime(doObj.getCreatedTime());
        return dto;
    }
}
