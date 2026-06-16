package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机构池与机构关联表DO
 */
@Data
@TableName("t_inst_pool_institution")
public class InstPoolInstitutionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机构池ID
     */
    private Long poolId;

    /**
     * 机构ID
     */
    private Long institutionId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入池时间
     */
    private LocalDateTime addedTime;
}
