package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class MenuUpdateParentCmd {
    private Long id;
    private Long newParentId;
}
