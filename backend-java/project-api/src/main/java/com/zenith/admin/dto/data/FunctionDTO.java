package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class FunctionDTO extends DTO {
    private Long id;
    private Long menuId;
    private String name;
    private String type;
    private String permission;
    private Integer sort;
    private Integer status;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
}
