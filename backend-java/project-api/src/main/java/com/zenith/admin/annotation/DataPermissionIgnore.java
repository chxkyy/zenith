package com.zenith.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过数据权限过滤注解
 * <p>
 * 标注在方法或类上，显式声明该方法/类不需要进行数据权限过滤。
 * 适用于公开数据、字典数据、系统配置等无需权限控制的场景。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 字典数据查询 — 不需要数据权限过滤
 * @DataPermissionIgnore
 * public List<DictDTO> listAll() { ... }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermissionIgnore {
}
