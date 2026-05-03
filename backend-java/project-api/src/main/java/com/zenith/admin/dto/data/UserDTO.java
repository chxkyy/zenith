package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.OrgName;
import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.annotation.UserName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户数据传输对象
 * 
 * 用于前后端数据交互
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends DTO {
    private Long id;
    private String username;
    private String email;
    private Integer status;
    private String role;
    
    /**
     * 角色名称（自动翻译）
     */
    @RoleName(roleId = "role", separator = ",")
    private String roleNames;
    
    /**
     * 所属组织ID
     * 关联 t_sys_org.id
     */
    private Long orgId;
    
    /**
     * 所属组织名称（自动翻译）
     * 根据 orgId 自动查询组织名称并填充
     */
    @OrgName(orgId = "orgId")
    private String orgName;
    
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
}
