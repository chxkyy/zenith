package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperLogEntity {
    private Long id;
    private String module;
    private String content;
    private String operator;
    private String ip;
    private String result;
    private String remark;
    private LocalDateTime createdAt;
}
