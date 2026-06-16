package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String type;

    private String status;
}
