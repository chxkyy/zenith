package com.zenith.admin.dto.data;

import lombok.Data;

@Data
public class OnlineUserDTO {
    private Long id;
    private Long userId;
    private String token;
    private Long loginTime;
    private Long lastAccessTime;
    private String ip;
}
