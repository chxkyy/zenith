package com.zenith.admin.dto.system.cmd;

import lombok.Data;

@Data
public class TaskApproveCmd {

    private Long taskId;

    private String opinion;
}
