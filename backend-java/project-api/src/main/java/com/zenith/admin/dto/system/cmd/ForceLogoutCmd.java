package com.zenith.admin.dto.system.cmd;

import lombok.Data;

@Data
public class ForceLogoutCmd {
    private String sessionId;
}
