package com.zenith.admin.dto.system.cmd;

import lombok.Data;

@Data
public class MenuReorderCmd {
    private Long id;
    private Integer targetIndex;
}
