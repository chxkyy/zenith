package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 准入操作日志DTO
 */
@Data
public class InstAdmissionLogDTO extends DTO {
    private Long id;
    private Long admissionId;
    private String action;
    private Long operatorId;
    private String operatorName;
    private String detail;
    private LocalDateTime createdTime;
}
