package com.zenith.admin.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class LoginLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String ip;
    private String status;
    private String msg;
    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
}
