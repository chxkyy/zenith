package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_sys_online_user")
public class OnlineUserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ip;
    private LocalDateTime lastAccessTime;
    private LocalDateTime loginTime;
    private String token;
    private Long userId;
}