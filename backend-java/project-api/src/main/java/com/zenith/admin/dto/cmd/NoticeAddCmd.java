package com.zenith.admin.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeAddCmd {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    private String type;

    private String status;
}
