package com.zenith.admin.dto.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDTO {

    private Long id;

    private String title;

    private String type;

    private String author;

    private String content;

    private String status;

    private String remark;

    private Boolean isPinned;

    private Integer readCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
