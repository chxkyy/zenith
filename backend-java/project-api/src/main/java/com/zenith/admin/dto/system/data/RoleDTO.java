package com.zenith.admin.dto.system.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDTO extends DTO {
    private Long createUserId;

    @UserName(userId = "createUserId")
    private String createUserName;
    private LocalDateTime createdTime;
    private String description;
    private Long id;
    private Integer memberCount;
    private String name;
    private Integer status;
    private LocalDateTime updateTime;
    private Long updateUserId;

    @UserName(userId = "updateUserId")
    private String updateUserName;
}
