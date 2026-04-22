package com.zenith.admin.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_sys_dict_item")
public class DictItemDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;

    private String label;

    private String dictValue;

    private Integer sort;

    private Integer status;

    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
