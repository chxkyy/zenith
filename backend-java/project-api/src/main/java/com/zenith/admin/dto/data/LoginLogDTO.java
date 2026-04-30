package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import com.zenith.admin.annotation.UserName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLogDTO extends DTO {
    private Long id;
    private String username;
    private String ip;
    private String status;
    private String msg;
    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;
    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
