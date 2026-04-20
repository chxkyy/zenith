package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_sys_oper_log")
public class OperLogDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String module;
    private String content;
    private String operator;
    private String ip;
    private String result;
    private String remark;
    private LocalDateTime createdAt;
}
