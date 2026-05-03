package com.zenith.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组织名称自动翻译注解
 * 
 * 使用方式：
 * 在 DTO 字段上标注此注解，AOP 会自动根据 orgId 查询组织名称并填充
 * 
 * 示例：
 * <pre>
 * &#64;OrgName(orgId = "orgId")
 * private String orgName;
 * </pre>
 * 
 * 工作原理：
 * 1. 读取注解中指定的 orgId 字段值（如 orgId = 123）
 * 2. 根据 ID 查询 t_sys_org 表获取组织名称
 * 3. 将组织名称设置到当前字段（orgName）
 * 
 * 性能优化：
 * - 使用 Caffeine 缓存，默认缓存 10 分钟
 * - 最大缓存 500 条记录
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OrgName {
    /**
     * 组织ID字段名
     * 用于指定从哪个字段获取组织ID
     * 
     * 示例：如果 DTO 中有 private Long orgId; 则填写 "orgId"
     */
    String orgId();
}
