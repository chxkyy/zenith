package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机构池详情DTO
 */
@Data
public class InstPoolDTO extends DTO {
    private Long id;
    private String name;
    private String poolType;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Integer status; // 1=启用 0=停用

    /**
     * 关联机构数量
     */
    private Integer institutionCount;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}
