package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class MenuReorderCmd {
    private Long id;
    private Integer targetIndex;
}
