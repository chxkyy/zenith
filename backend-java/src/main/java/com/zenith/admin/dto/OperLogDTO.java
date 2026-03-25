package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperLogDTO extends DTO {
    private Long id;
    private String module;
    private String content;
    private String operator;
    private String ip;
    private String result;
    private String remark;
    private LocalDateTime createdAt;
}
