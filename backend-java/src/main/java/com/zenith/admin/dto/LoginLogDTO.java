package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLogDTO extends DTO {
    private Long id;
    private String username;
    private String ip;
    private String status;
    private String msg;
    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;
}
