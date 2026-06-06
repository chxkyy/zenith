package com.zenith.admin.dto.cmd;

import lombok.Data;

@Data
public class ForceLogoutCmd {
    private String sessionId;
}
