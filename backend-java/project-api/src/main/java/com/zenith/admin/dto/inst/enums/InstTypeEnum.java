package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机构类型枚举
 */
@Getter
@AllArgsConstructor
public enum InstTypeEnum {

    FUND_COMPANY("FUND_COMPANY", "基金公司"),
    ASSET_MANAGER("ASSET_MANAGER", "资管公司"),
    SECURITIES("SECURITIES", "证券公司"),
    INSURANCE("INSURANCE", "保险公司"),
    BANK_WEALTH("BANK_WEALTH", "银行理财子"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
