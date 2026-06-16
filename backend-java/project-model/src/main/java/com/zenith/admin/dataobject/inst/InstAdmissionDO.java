package com.zenith.admin.dataobject.inst;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_inst_admission")
public class InstAdmissionDO {

    @TableId(type = IdType.AUTO)
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

    private String targetPoolIds;

    private Object basicInfo;

    private String status;

    private Long scorerId;

    private Object scoreResult;

    private Long approverId;

    private String approvalOpinion;

    private String rejectionReason;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
}
