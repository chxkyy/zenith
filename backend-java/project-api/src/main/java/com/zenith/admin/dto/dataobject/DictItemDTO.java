package com.zenith.admin.dto.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DictItemDTO {

    private Long id;

    private String type;

    private String label;

    private String dictValue;

    private Integer sort;

    private Integer status;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
