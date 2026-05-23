package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_sys_role_org")
public class RoleOrgDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long orgId;
    private LocalDateTime createdTime;
}
