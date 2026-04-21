package com.zenith.admin.dto.data;

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

    private LocalDateTime createdAt;
}
