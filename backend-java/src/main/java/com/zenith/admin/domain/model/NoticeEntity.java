package com.zenith.admin.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeEntity {
    private Long id;
    private String title;
    private String type;
    private String author;
    private String status;
    private LocalDateTime createdAt;
}
