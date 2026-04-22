package com.zenith.admin.dto.data;

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

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
