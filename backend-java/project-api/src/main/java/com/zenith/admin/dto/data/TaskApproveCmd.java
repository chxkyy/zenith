package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class TaskApproveCmd {

    private Long taskId;

    private String opinion;
}
