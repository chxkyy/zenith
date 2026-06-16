package com.zenith.admin.dto.system.data;

import lombok.Data;

@Data
public class OnlineUserDTO {
    private String sessionId;
    private Long userId;
    private String username;
    private String ip;
    private String location;
    private String userAgent;
    private String browser;
    private Long loginTime;
    private Long lastAccessTime;
    private Boolean current;
}
