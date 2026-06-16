package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 材料类别枚举
 */
@Getter
@AllArgsConstructor
public enum InstMaterialCategoryEnum {

    BUSINESS_LICENSE("BUSINESS_LICENSE", "营业执照/登记证书"),
    FINANCIAL_LICENSE("FINANCIAL_LICENSE", "金融许可证/备案证明"),
    FINANCIAL_REPORT("FINANCIAL_REPORT", "最近一期财务报表"),
    ARTICLES("ARTICLES", "公司章程"),
    QUALIFICATION_CERT("QUALIFICATION_CERT", "主要人员资质证明"),
    INTERNAL_CONTROL("INTERNAL_CONTROL", "内部控制制度文件"),
    OTHER("OTHER", "其他材料");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstMaterialCategoryEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstMaterialCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
