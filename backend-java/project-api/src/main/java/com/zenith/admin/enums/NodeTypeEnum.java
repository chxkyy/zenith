package com.zenith.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeTypeEnum {

    NORMAL(1, "普通审批"),
    COUNTERSIGN(2, "会签");

    private final Integer code;
    private final String name;

    public static NodeTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NodeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
