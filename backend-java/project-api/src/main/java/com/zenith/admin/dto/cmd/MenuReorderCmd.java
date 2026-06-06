package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class MenuReorderCmd {
    private Long id;
    private Integer targetIndex;
}
