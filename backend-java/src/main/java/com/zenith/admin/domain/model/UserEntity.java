package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserEntity {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Integer status;
    private String role;
    private String orgName;
    private LocalDateTime createdAt;
}
