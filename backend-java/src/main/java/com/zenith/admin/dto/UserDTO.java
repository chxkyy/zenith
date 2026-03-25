package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

@Data
public class UserDTO extends DTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Integer status;
    private String role;
    private String orgName;
}
