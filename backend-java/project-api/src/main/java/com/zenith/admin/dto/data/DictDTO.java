package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class DictDTO extends DTO {
    private Long id;
    private String name;
    private String type;
    private Integer status;
    private String remark;
    private java.time.LocalDateTime createdTime;
    private java.time.LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
}
