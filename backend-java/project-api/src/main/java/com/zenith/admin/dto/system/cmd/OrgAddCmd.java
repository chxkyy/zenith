package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrgAddCmd {

    @NotBlank(message = "组织名称不能为空")
    private String name;

    private Long parentId;

    private Integer sort;
}
