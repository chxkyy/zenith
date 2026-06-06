package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class TaskApproveCmd {

    private Long taskId;

    private String opinion;
}
