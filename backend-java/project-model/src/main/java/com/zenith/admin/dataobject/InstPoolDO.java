package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机构池DO
 */
@Data
@TableName("t_inst_pool")
public class InstPoolDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 池名称
     */
    private String name;

    /**
     * 池类型
     */
    private String poolType;

    /**
     * 描述说明
     */
    private String description;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 状态：1=启用 0=停用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
}
