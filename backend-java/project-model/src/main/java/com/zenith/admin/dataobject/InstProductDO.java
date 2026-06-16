package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 产品记录DO
 */
@Data
@TableName("t_inst_product")
public class InstProductDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属机构ID
     */
    private Long institutionId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 合作状态
     */
    private String cooperationStatus;

    /**
     * 合作开始日期
     */
    private Date cooperationStartDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 联系人
     */
    private String contactPerson;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
}
