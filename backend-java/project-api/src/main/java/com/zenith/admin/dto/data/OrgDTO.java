package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;

@Data
public class OrgDTO extends DTO {
    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private Integer status;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
