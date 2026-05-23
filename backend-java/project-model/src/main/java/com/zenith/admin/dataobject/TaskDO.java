package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_wf_task")
public class TaskDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long processInstanceId;

    private Integer nodeOrder;

    private String nodeName;

    private Integer nodeType;

    private Long assigneeId;

    private Integer assigneeType;

    private Long parentTaskId;

    private Integer status;

    private String opinion;

    private Integer actionType;

    private LocalDateTime actionTime;

    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
