package com.zenith.admin.dto.dataobject;

import lombok.Data;

@Data
public class NoticeStatusUpdateCmd {
    private Long id;
    private String status;
}
