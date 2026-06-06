package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class ProcessInstanceCreateCmd {

    private Long processTemplateId;

    private String title;

    private String formData;
}
