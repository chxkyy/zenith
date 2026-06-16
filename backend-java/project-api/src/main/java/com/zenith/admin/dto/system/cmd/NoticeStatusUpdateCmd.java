package com.zenith.admin.dto.system.cmd;

import lombok.Data;

@Data
public class NoticeStatusUpdateCmd {
    private Long id;
    private String status;
}
