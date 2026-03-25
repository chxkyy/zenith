package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleEntity {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Integer memberCount;
    private LocalDateTime createdAt;
}
