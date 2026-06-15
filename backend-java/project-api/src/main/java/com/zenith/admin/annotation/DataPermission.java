package com.zenith.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限控制注解
 * <p>
 * 标注在 Service 方法或 Mapper 方法上，声明该方法需要执行数据权限过滤。
 * 框架会根据指定的策略自动改写 SQL，追加权限过滤条件。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 查询接口 — 使用策略一（纯组织架构控制）
 * @DataPermission(strategy = DataPermissionStrategy.ORG)
 * public PageInfo<CustomerDTO> listByPage(CustomerPageQuery query) { ... }
 *
 * // 查询接口 — 使用策略二（人员-数据绑定 + 组织架构）
 * @DataPermission(strategy = DataPermissionStrategy.OWNER_ORG)
 * public PageInfo<ProductDTO> listByPage(ProductPageQuery query) { ... }
 * }</pre>
 *
 * <h3>行为说明：</h3>
 * <ul>
 *   <li>未标注此注解的方法默认不进行数据权限过滤（向后兼容）</li>
 *   <li>与 {@link DataPermissionIgnore} 同时存在时，@DataPermissionIgnore 优先</li>
 * </ul>
 *
 * @see DataPermissionStrategy
 * @see DataPermissionIgnore
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * 权限控制策略
     * <p>默认使用 OWNER_ORG（人员-数据绑定 + 组织架构混合控制）</p>
     */
    DataPermissionStrategy strategy() default DataPermissionStrategy.OWNER_ORG;
}
