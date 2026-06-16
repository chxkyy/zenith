package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

/**
 * 简要机构信息DTO（用于列表展示）
 */
@Data
public class SimpleInstitutionDTO extends DTO {
    private Long id;
    private String fullName;
    private String shortName;
    private String instType;
    private String cooperationStatus;
    private String creditCode;
}
