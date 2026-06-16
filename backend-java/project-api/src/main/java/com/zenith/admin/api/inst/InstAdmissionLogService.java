package com.zenith.admin.api.inst;

import com.zenith.admin.dto.inst.data.InstAdmissionLogDTO;

import java.util.List;

/**
 * 准入申请操作日志服务接口
 *
 * <p>记录和查询准入申请过程中的所有操作日志，
 * 用于审计追踪和问题排查。</p>
 */
public interface InstAdmissionLogService {

    /**
     * 追加操作日志
     *
     * <p>记录一次操作到指定申请单的日志中。</p>
     *
     * @param admissionId  申请单ID
     * @param action       操作类型（CREATE、SUBMIT、SCORE、APPROVE、REJECT、RETURN、WITHDRAW 等）
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     * @param detail       操作详情（可以是 Map 或 JSON 字符串）
     */
    void append(Long admissionId, String action, Long operatorId, String operatorName, Object detail);

    /**
     * 查询指定申请的操作日志
     *
     * @param admissionId 申请单ID
     * @return 日志列表（按时间倒序）
     */
    List<InstAdmissionLogDTO> listByAdmissionId(Long admissionId);
}
