package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 机构详情DTO（包含所有字段及关联信息）
 */
@Data
public class InstInstitutionDTO extends DTO {

    // ========== 基础信息 ==========
    private Long id;
    private String fullName;
    private String shortName;
    private String creditCode;
    private String instType;
    private LocalDate establishDate;
    private String registeredCapital;
    private String legalRepresentative;
    private String registeredAddress;
    private String contactPhone;
    private String contactEmail;
    private String logoUrl;
    private String cooperationStatus; // pending/cooperating/terminated/suspended/pending_admit

    // ========== 系统字段 ==========
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private String createUserName;
    private Long updateUserId;
    private String updateUserName;

    // ========== 扩展关联 ==========
    /**
     * 所属机构池列表
     */
    private List<SimplePoolDTO> pools;

    /**
     * 产品数量
     */
    private Integer productCount;
}
