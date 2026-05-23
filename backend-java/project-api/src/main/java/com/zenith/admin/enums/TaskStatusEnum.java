package com.zenith.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatusEnum {

    PENDING(0, "待处理"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已退回"),
    COUNTERSIGNED(3, "已加签"),
    TERMINATED(4, "已终止");

    private final Integer code;
    private final String name;

    public static TaskStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
