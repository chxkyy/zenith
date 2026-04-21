package com.zenith.admin.dto.data;

import com.zenith.admin.annotation.DictTranslate;
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

    @DictTranslate(source = "status", dictType = "notice_status")
    private String statusName;

    private String remark;

    private Boolean isPinned;

    private Integer readCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
