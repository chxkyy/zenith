package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户数据对象
 * 
 * 对应数据库表：t_sys_user
 */
@Data
@TableName("t_sys_user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    private Integer status;
    private String role;
    
    /**
     * 所属组织ID
     * 关联 t_sys_org.id
     */
    private Long orgId;
    
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long updateUserId;
    private Long createUserId;
}
