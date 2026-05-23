package com.zenith.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessStatusEnum {

    DRAFT(0, "草稿"),
    IN_PROGRESS(1, "审批中"),
    PASSED(2, "已通过"),
    REVOKED(3, "已撤销"),
    RETURNED(4, "已退回"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String name;

    public static ProcessStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ProcessStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
