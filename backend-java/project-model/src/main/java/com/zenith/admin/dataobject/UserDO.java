package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_sys_user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Integer status;
    private String role;
    private String orgName;
    private LocalDateTime createdAt;
}
