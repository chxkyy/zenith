package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class NoticeStatusUpdateCmd {
    private Long id;
    private String status;
}
