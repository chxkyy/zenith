package com.zenith.admin.dto;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeDTO extends DTO {
    private Long id;
    private String title;
    private String type;
    private String author;
    private String status;
    private LocalDateTime createdAt;
}
