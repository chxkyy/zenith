package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorLogEntity {
    private Long id;
    private String module;
    private String ip;
    private String errorMsg;
    private String stackTrace;
    private LocalDateTime createdAt;
}
