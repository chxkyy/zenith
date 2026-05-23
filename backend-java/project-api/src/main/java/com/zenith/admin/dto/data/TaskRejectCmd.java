package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class TaskRejectCmd {

    private Long taskId;

    private String opinion;
}
