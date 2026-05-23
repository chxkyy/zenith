package com.zenith.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApproverTypeEnum {

    ROLE(1, "角色"),
    USER(2, "用户"),
    SUPERIOR(3, "发起人上级");

    private final Integer code;
    private final String name;

    public static ApproverTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ApproverTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
