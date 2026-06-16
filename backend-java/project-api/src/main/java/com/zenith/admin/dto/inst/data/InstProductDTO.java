package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 产品记录DTO
 */
@Data
public class InstProductDTO extends DTO {
    private Long id;
    private Long institutionId;

    /**
     * 所属机构名称
     */
    private String institutionName;

    private String productName;
    private String productCode;
    private String productType;
    private String cooperationStatus;
    private Date cooperationStartDate;
    private Date endDate;
    private String contactPerson;

    // 系统字段
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
}
