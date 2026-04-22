package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_sys_notice")
public class NoticeDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String type;

    private String author;

    private String content;

    private String status;

    private String remark;

    private Boolean isPinned;

    private Integer readCount;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
