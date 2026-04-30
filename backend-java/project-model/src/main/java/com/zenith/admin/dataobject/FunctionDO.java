package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_sys_function")
public class FunctionDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long menuId;
    private String name;
    private String type;
    private String permission;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
}
