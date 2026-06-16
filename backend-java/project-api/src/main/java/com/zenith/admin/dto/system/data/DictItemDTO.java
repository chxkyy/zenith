package com.zenith.admin.dto.system.data;

import com.zenith.admin.annotation.UserName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DictItemDTO {

    private Long id;

    private String type;

    private String label;

    private String dictValue;

    private Integer sort;

    private Integer status;

    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;

    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
