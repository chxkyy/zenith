package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员-数据权限绑定表 DO
 * <p>
 * 对应数据库表 t_data_permission，仅用于策略二（OWNER_ORG）。
 * 记录用户与业务数据实体的负责人关系（多对多）。
 * </p>
 *
 * @see com.zenith.admin.annotation.DataPermission
 */
@Data
@TableName("t_data_permission")
public class DataPermissionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 负责人用户ID，关联 t_sys_user.id */
    private Long userId;

    /**
     * 业务数据类型标识
     * <p>如 customer（客户）、product（产品）等，预留扩展多数据类型</p>
     */
    private String dataType;

    /** 业务数据记录的主键ID */
    private Long dataId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
}
