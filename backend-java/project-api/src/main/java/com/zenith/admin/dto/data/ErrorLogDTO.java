package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
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
    private Long updateUserId;
}
