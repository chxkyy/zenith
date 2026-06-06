package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class TaskRejectCmd {

    private Long taskId;

    private String opinion;
}
