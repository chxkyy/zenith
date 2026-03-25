package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DictEntity {
    private Long id;
    private String type;
    private String label;
    private String value;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
}
