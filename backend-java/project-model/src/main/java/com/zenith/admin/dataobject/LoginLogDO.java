package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_sys_login_log")
public class LoginLogDO {
    @TableId(type = IdType.AUTO)
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
    private Long updateUserId;
}
