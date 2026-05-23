package com.zenith.admin.dto.data;

import com.alibaba.cola.dto.PageQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {
    private String keyword; // 关键词搜索（用户名、昵称、手机号、邮箱）
    private Long orgId; // 组织ID筛选
    private Long currentUserId; // 当前用户ID（用于数据权限过滤）

    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status; // 状态筛选

    private String sortField; // 排序字段
    private String sortOrder; // 排序方向（asc/desc）

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
