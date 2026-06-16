package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合作机构DO
 */
@Data
@TableName("t_inst_institution")
public class InstInstitutionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机构全称
     */
    private String fullName;

    /**
     * 机构简称
     */
    private String shortName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 机构类型
     */
    private String instType;

    /**
     * 成立日期
     */
    private LocalDate establishDate;

    /**
     * 注册资本
     */
    private String registeredCapital;

    /**
     * 法定代表人
     */
    private String legalRepresentative;

    /**
     * 注册地址
     */
    private String registeredAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * Logo地址
     */
    private String logoUrl;

    /**
     * 合作状态：pending/cooperating/terminated/suspended/pending_admit
     */
    private String cooperationStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
}
