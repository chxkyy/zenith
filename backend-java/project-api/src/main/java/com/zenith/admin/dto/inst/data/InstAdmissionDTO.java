package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请单详情DTO
 */
@Data
public class InstAdmissionDTO extends DTO {

    // ========== 基础字段 ==========
    private Long id;
    private String admissionNo;
    private Long processInstanceId;
    private String managerName;
    private String managerType;
    private String creditCode;
    private String registeredCapital;
    private LocalDate establishDate;
    private String legalRepresentative;
    private String registeredAddress;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private List<Long> targetPoolIds;
    private String status; // DRAFT/PENDING_REVIEW/PENDING_APPROVAL/APPROVED/REJECTED/WITHDRAWN

    // ========== 审批相关 ==========
    private Long scorerId;
    private String scorerName;
    private Long approverId;
    private String approverName;
    private String approvalOpinion;
    private String rejectionReason;

    // ========== 系统字段 ==========
    private Long createUserId;
    private String creatorName;
    private LocalDateTime createdTime;
    private Long updateUserId;
    private LocalDateTime updateTime;

    // ========== 子资源 ==========
    /**
     * 材料列表
     */
    private List<InstAdmissionMaterialDTO> materials;

    /**
     * 操作日志
     */
    private List<InstAdmissionLogDTO> logs;
}
