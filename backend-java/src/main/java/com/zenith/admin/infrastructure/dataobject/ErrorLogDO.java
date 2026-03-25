package com.zenith.admin.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_error_log")
public class ErrorLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String ip;
    private String errorMsg;
    private String stackTrace;
    private LocalDateTime createdAt;
}
