package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_file")
public class FileDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String originalName;

    private String path;

    private String type;

    private Long size;

    private String uploader;

    private LocalDateTime createdAt;
}
