package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class DictDTO extends DTO {
    private Long id;
    private String type;
    private String label;
    private String value;
    private Integer sort;
    private Integer status;
}
