package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

/**
 * 简要机构池信息DTO（用于机构详情中的池列表展示）
 */
@Data
public class SimplePoolDTO extends DTO {
    private Long id;
    private String name;
    private String poolType;
}
