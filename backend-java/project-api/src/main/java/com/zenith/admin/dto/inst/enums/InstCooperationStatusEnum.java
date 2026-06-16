package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合作状态枚举
 */
@Getter
@AllArgsConstructor
public enum InstCooperationStatusEnum {

    PENDING("PENDING", "待准入"),
    COOPERATING("COOPERATING", "合作中"),
    TERMINATED("TERMINATED", "已终止"),
    SUSPENDED("SUSPENDED", "暂停合作");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstCooperationStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstCooperationStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
