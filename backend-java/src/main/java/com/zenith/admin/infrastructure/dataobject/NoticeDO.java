package com.zenith.admin.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class NoticeDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String type;
    private String author;
    private String status;
    private LocalDateTime createdAt;
}
