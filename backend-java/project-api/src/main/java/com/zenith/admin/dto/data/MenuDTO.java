package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;

@Data
public class MenuDTO extends DTO {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    private String type;
    private String permission;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
