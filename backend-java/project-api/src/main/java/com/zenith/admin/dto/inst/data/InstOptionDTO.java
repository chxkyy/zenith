package com.zenith.admin.dto.inst.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;

import java.util.List;

/**
 * 下拉选项DTO（枚举值集合）
 */
@Data
public class InstOptionDTO extends DTO {

    /**
     * 池类型列表（公募池/私募池/专户池等）
     */
    private List<String> poolTypes;

    /**
     * 机构类型列表
     */
    private List<String> instTypes;

    /**
     * 产品类型列表
     */
    private List<String> productTypes;

    /**
     * 合作状态列表
     */
    private List<String> cooperationStatuses;

    /**
     * 准入申请状态列表
     */
    private List<String> admissionStatuses;

    /**
     * 材料分类列表
     */
    private List<String> materialCategories;
}
