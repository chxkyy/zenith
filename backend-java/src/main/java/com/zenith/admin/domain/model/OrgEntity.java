package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrgEntity {
    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
}
