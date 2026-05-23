package com.zenith.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionTypeEnum {

    SUBMIT(1, "提交申请"),
    APPROVE(2, "审批通过"),
    REJECT(3, "退回"),
    COUNTERSIGN(4, "加签"),
    TERMINATE(5, "提前终止"),
    REVOKE(6, "撤销"),
    RESUBMIT(7, "重新提交"),
    CANCEL(8, "取消");

    private final Integer code;
    private final String name;

    public static ActionTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ActionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
