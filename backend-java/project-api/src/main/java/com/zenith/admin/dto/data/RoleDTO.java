package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDTO extends DTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Integer memberCount;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
