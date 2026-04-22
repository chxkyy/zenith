package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.RoleName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO extends DTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Integer status;
    private String role;
    @RoleName(roleId = "role", separator = ",")
    private String roleNames;
    private String orgName;
    private Long createUserId;
    private Long updateUserId;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
}
