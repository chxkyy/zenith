package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.annotation.UserName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends DTO {
    private Long id;
    private String username;
    private String email;
    private Integer status;
    private String role;
    @RoleName(roleId = "role", separator = ",")
    private String roleNames;
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
