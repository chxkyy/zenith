package com.zenith.admin.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_dict")
public class DictDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String label;
    @TableField("dict_value")
    private String value;
    private Integer sort;
    private Integer status;
    private LocalDateTime createdAt;
}
