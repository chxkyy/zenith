package com.zenith.admin.dto.inst.qry;

import com.alibaba.cola.dto.PageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品列表分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstProductPageQuery extends PageQuery {

    /**
     * 所属机构ID（可选）
     */
    private Long institutionId;

    /**
     * 合作状态
     */
    private String cooperationStatus;

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
