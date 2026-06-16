package com.zenith.admin.service.inst.executor.cmd;

import com.alibaba.fastjson2.JSON;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.inst.InstAdmissionLogDO;
import com.zenith.admin.mapper.inst.InstAdmissionLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 日志追加执行器
 *
 * <p>向准入申请的操作日志中追加一条记录。</p>
 */
@Component
@RequiredArgsConstructor
public class InstAdmissionLogAppendCmdExe {

    private final InstAdmissionLogMapper logMapper;

    /**
     * 执行日志追加操作
     *
     * @param admissionId  申请单ID
     * @param action       操作类型
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     * @param detail       操作详情（Map 或 JSON 字符串）
     */
    public void execute(Long admissionId, String action, Long operatorId, String operatorName, Object detail) {
        InstAdmissionLogDO logDO = new InstAdmissionLogDO();
        logDO.setAdmissionId(admissionId);
        logDO.setAction(action);
        logDO.setOperatorId(operatorId);
        logDO.setOperatorName(operatorName);
        logDO.setDetail(detail instanceof String ? (String) detail : JSON.toJSONString(detail));
        logDO.setCreatedTime(LocalDateTime.now());

        logMapper.insert(logDO);
    }
}
