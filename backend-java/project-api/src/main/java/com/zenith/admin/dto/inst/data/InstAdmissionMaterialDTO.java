package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 准入申请材料记录DTO
 */
@Data
public class InstAdmissionMaterialDTO extends DTO {
    private Long id;
    private Long admissionId;
    private String materialCategory;
    private String materialName;
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private Integer sortOrder;
    private LocalDateTime createdTime;
}
