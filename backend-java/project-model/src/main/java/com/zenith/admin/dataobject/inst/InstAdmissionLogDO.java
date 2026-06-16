package com.zenith.admin.dataobject.inst;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_inst_admission_log")
public class InstAdmissionLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long admissionId;

    private String action;

    private Long operatorId;

    private String operatorName;

    private String detail;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
