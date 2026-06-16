package com.zenith.admin.dto.system.qry;

import lombok.Data;

@Data
public class LoginQuery {
    private String loginId;
    private String password;
    private String ip;
    private String userAgent;
}
