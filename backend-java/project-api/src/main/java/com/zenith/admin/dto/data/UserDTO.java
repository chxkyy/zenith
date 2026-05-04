package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.OrgName;
import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.annotation.UserName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends DTO {
    private Long createUserId;

    @UserName(userId = "createUserId")
    private String createUserName;
    private java.time.LocalDateTime createdTime;
    private String email;
    private Long id;
    private String loginId;
    /**
     * 用户中文名（显示名称）
     */
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
    /**
     * 角色名称（自动翻译）
     */
    @RoleName(roleId = "roles", separator = ",")
    private String roleNames;
    /**
     * 用户角色编码列表（来自 t_sys_user_role 关联表）
     * 如: ["ROLE_ADMIN", "ROLE_USER"]
     */
    private List<String> roles;
    private Integer status;
    private java.time.LocalDateTime updateTime;
    private Long updateUserId;

    @UserName(userId = "updateUserId")
    private String updateUserName;
    private String username;
}
