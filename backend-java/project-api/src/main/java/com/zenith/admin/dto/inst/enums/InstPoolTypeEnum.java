package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 池类型枚举
 */
@Getter
@AllArgsConstructor
public enum InstPoolTypeEnum {

    PUBLIC_FUND("PUBLIC_FUND", "公募基金"),
    PRIVATE_FUND("PRIVATE_FUND", "私募基金"),
    SPECIAL_ACCOUNT("SPECIAL_ACCOUNT", "专户产品"),
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstPoolTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstPoolTypeEnum type : values()) {
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
