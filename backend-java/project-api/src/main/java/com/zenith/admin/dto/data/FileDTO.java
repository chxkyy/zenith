package com.zenith.admin.dto.data;

import com.zenith.admin.annotation.UserName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDTO {

    private Long id;

    private String name;

    private String originalName;

    private String path;

    private String type;

    private Long size;

    private String uploader;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private Long createUserId;
    @UserName(userId = "createUserId")
    private String createUserName;

    private Long updateUserId;
    @UserName(userId = "updateUserId")
    private String updateUserName;
}
