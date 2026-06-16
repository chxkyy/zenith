package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserAddCmd {

    @NotBlank(message = "登录账号不能为空")
    private String loginId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String email;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    private Integer status;

    private List<String> roles;
}
