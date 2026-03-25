package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLogEntity {
    private Long id;
    private String username;
    private String ip;
    private String status;
    private String msg;
    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
}
