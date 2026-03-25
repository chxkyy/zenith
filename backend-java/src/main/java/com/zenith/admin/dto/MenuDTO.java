package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class MenuDTO extends DTO {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private String type;
    private String permission;
}
