package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MenuEntity {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private String type;
    private String permission;
    private LocalDateTime createdAt;
}
