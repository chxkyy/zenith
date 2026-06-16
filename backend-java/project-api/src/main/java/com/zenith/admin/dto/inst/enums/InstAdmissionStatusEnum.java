package com.zenith.admin.dto.inst.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 准入申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum InstAdmissionStatusEnum {

    DRAFT("DRAFT", "草稿"),
    PENDING_REVIEW("PENDING_REVIEW", "待评分"),
    PENDING_APPROVAL("PENDING_APPROVAL", "待审批"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    WITHDRAWN("WITHDRAWN", "已撤销");

    private final String code;
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static InstAdmissionStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstAdmissionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否可编辑（草稿或已撤销状态）
     *
     * @return 是否可编辑
     */
    public boolean isEditable() {
        return this == DRAFT || this == WITHDRAWN;
    }

    /**
     * 判断是否为终态（已通过或已拒绝）
     *
     * @return 是否为终态
     */
    public boolean isFinalState() {
        return this == APPROVED || this == REJECTED;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
