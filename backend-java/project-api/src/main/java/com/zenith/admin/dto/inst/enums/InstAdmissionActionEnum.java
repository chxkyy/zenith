package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum InstAdmissionActionEnum {

    CREATE("CREATE", "创建申请"),
    SUBMIT("SUBMIT", "提交评审"),
    SCORE("SCORE", "评分完成"),
    SUBMIT_APPROVE("SUBMIT_APPROVE", "提交审批"),
    APPROVE("APPROVE", "审批通过"),
    REJECT("REJECT", "审批驳回"),
    RETURN("RETURN", "退回补正"),
    WITHDRAW("WITHDRAW", "撤销申请");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstAdmissionActionEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstAdmissionActionEnum action : values()) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
