package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.PageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionPageQuery extends PageQuery {
    private String keyword; // 关键词搜索（功能名称）
    private String type; // 类型：button / field
    private Long menuId; // 所属菜单ID

    @Min(value = 1, message = "页码必须大于0")
    @Override
    public int getPageIndex() {
        return super.getPageIndex();
    }

    @Min(value = 1, message = "每页条数必须大于0")
    @Max(value = 100, message = "每页条数不能超过100")
    @Override
    public int getPageSize() {
        return super.getPageSize();
    }
}
