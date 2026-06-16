package com.zenith.admin.dto.inst.qry;

import com.alibaba.cola.dto.PageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机构列表分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstitutionPageQuery extends PageQuery {

    /**
     * 机构池ID（可选）
     */
    private Long poolId;

    /**
     * 关键词（模糊搜索：名称/信用代码）
     */
    private String keyword;

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
