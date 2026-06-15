package com.zenith.admin.aspect;

import com.zenith.admin.annotation.DataPermission;
import com.zenith.admin.annotation.DataPermissionIgnore;
import com.zenith.admin.annotation.DataPermissionStrategy;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.service.system.DataPermissionScopeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据权限控制 AOP 切面
 * <p>
 * 拦截标注了 {@link DataPermission} 注解的方法，在方法执行前计算当前用户的权限范围，
 * 并将结果存入 {@link DataPermissionHelper} 的 ThreadLocal 中，
 * 供 {@link com.zenith.admin.aspect.DataPermissionInnerInterceptor} 在 SQL 执行时读取并改写 SQL。
 * </p>
 *
 * <h3>执行流程：</h3>
 * <ol>
 *   <li>检测方法/类上的 @DataPermission 或 @DataPermissionIgnore 注解</li>
 *   <li>@DataPermissionIgnore → 跳过权限过滤</li>
 *   <li>@DataPermission → 从 UserContext 取当前用户 ID → 计算权限范围 → 存入 ThreadLocal</li>
 *   <li>执行原方法（SQL 执行时 InnerInterceptor 自动改写）</li>
 *   <li>清理 ThreadLocal</li>
 * </ol>
 *
 * @see DataPermission
 * @see DataPermissionIgnore
 * @see DataPermissionHelper
 */
@Slf4j
@Aspect
@Component
@Order(1) // 确保在其他切面之前执行
@RequiredArgsConstructor
public class DataPermissionAspect {

    private final DataPermissionScopeService dataPermissionScopeService;

    /**
     * 切点：拦截所有标注了 @DataPermission 或 @DataPermissionIgnore 的方法
     */
    @Around("@annotation(dataPermission) || @annotation(dataPermissionIgnore)")
    public Object aroundDataPermission(ProceedingJoinPoint joinPoint,
                                       DataPermission dataPermission,
                                       DataPermissionIgnore dataPermissionIgnore) throws Throwable {
        try {
            // 1. 如果标注了 @DataPermissionIgnore，跳过权限过滤
            if (dataPermissionIgnore != null) {
                DataPermissionHelper.setIgnore(true);
                return joinPoint.proceed();
            }

            // 2. 标注了 @DataPermission，执行权限过滤
            if (dataPermission != null) {
                return handleDataPermission(joinPoint, dataPermission);
            }

            // 3. 默认：不进行权限过滤（向后兼容）
            return joinPoint.proceed();

        } finally {
            // 4. 无论成功失败，清理 ThreadLocal 防止内存泄漏
            DataPermissionHelper.clear();
        }
    }

    /**
     * 处理数据权限过滤逻辑
     */
    private Object handleDataPermission(ProceedingJoinPoint joinPoint,
                                       DataPermission dataPermission) throws Throwable {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.warn("UserContext 中无用户信息，跳过数据权限过滤");
            return joinPoint.proceed();
        }

        DataPermissionStrategy strategy = dataPermission.strategy();

        switch (strategy) {
            case ORG -> {
                // 策略一：纯组织架构控制 — 计算可访问的组织ID列表
                List<Long> accessibleOrgIds = dataPermissionScopeService.getAccessibleOrgIds(userId);
                DataPermissionHelper.setOrgIds(accessibleOrgIds);
                log.debug("策略一(ORG)权限过滤: userId={}, orgIds={}", userId, accessibleOrgIds);
            }
            case OWNER_ORG -> {
                // 策略二：人员-数据绑定 + 组织架构 — 计算可访问的用户ID列表
                List<Long> accessibleUserIds = dataPermissionScopeService.getAccessibleUserIds(userId);
                DataPermissionHelper.setUserIds(accessibleUserIds);
                log.debug("策略二(OWNER_ORG)权限过滤: userId={}, userIds={}", userId, accessibleUserIds);
            }
        }

        return joinPoint.proceed();
    }
}
