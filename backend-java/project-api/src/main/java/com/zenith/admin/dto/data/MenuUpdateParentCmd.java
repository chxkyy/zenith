package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class MenuUpdateParentCmd {
    private Long id;
    private Long newParentId;
}
