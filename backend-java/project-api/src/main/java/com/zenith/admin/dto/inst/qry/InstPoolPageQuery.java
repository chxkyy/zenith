package com.zenith.admin.dto.inst.qry;

import com.alibaba.cola.dto.PageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机构池分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstPoolPageQuery extends PageQuery {

    /**
     * 池名称（模糊搜索）
     */
    private String name;

    /**
     * 池类型
     */
    private String poolType;

    /**
     * 状态：1=启用 0=停用
     */
    private Integer status;

    @Min(value = 1, message = "页码必须大于0")
    @Override
    public int getPageIndex() {
        return super.getPageIndex();
    }

    @Min(value = 1, message = "每页条数必须大于0")
    @Max(value = 1000, message = "每页条数不能超过1000")
    @Override
    public int getPageSize() {
        return super.getPageSize();
    }
}
