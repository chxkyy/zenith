package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class RoleDTO extends DTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Integer memberCount;
}
