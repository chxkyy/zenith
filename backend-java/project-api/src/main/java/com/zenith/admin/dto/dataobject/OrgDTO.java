package com.zenith.admin.dto.dataobject;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class OrgDTO extends DTO {
    private Long id;
    private Long parentId;
    private String name;
    private Integer sort;
    private Integer status;
}
