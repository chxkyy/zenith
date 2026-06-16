package com.zenith.admin.dto.system.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorLogDTO extends DTO {
    private Long id;
    private String module;
    private String ip;
    private String errorMsg;
    private String stackTrace;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
