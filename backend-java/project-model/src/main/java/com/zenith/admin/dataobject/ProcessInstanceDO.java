package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_wf_process_instance")
public class ProcessInstanceDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String processNo;

    private Long processTemplateId;

    private Integer processTemplateVersion;

    private String title;

    private String formData;

    private Integer status;

    private Long initiatorId;

    private Integer currentNodeOrder;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
