package com.zenith.admin.aspect;

import java.util.Collections;
import java.util.List;

/**
 * 数据权限过滤条件 ThreadLocal 持有者
 * <p>
 * 在 AOP 切面（{@link DataPermissionAspect}）中设置权限范围，
 * 在 MyBatis-Plus InnerInterceptor（{@link DataPermissionInnerInterceptor}）中读取并改写 SQL。
 * </p>
 *
 * <h3>使用方式（供内部组件使用，业务代码不应直接调用）：</h3>
 * <ul>
 *   <li>{@link #setOrgIds(List)} — 设置策略一的组织ID列表</li>
 *   <li>{@link #setUserIds(List)} — 设置策略二的用户ID列表</li>
 *   <li>{@link #setIgnore(boolean)} — 设置是否跳过权限过滤</li>
 *   <li>{@link #clear()} — 清理 ThreadLocal（必须在 finally 中调用）</li>
 * </ul>
 */
public final class DataPermissionHelper {

    private DataPermissionHelper() {
        // 工具类，禁止实例化
    }

    /** 策略一：可访问的组织 ID 列表 */
    private static final ThreadLocal<List<Long>> ORG_IDS = new ThreadLocal<>();

    /** 策略二：可访问的用户 ID 列表 */
    private static final ThreadLocal<List<Long>> USER_IDS = new ThreadLocal<>();

    /** 是否跳过权限过滤 */
    private static final ThreadLocal<Boolean> IGNORE = ThreadLocal.withInitial(() -> false);

    // ==================== 策略一（ORG） ====================

    public static void setOrgIds(List<Long> orgIds) {
        ORG_IDS.set(orgIds != null ? orgIds : Collections.emptyList());
    }

    public static List<Long> getOrgIds() {
        List<Long> ids = ORG_IDS.get();
        return ids != null ? ids : Collections.emptyList();
    }

    // ==================== 策略二（OWNER_ORG） ====================

    public static void setUserIds(List<Long> userIds) {
        USER_IDS.set(userIds != null ? userIds : Collections.emptyList());
    }

    public static List<Long> getUserIds() {
        List<Long> ids = USER_IDS.get();
        return ids != null ? ids : Collections.emptyList();
    }

    // ==================== 跳过控制 ====================

    public static void setIgnore(boolean ignore) {
        IGNORE.set(ignore);
    }

    public static boolean isIgnore() {
        Boolean ignore = IGNORE.get();
        return ignore != null && ignore;
    }

    // ==================== 状态检查 ====================

    /**
     * 是否存在待处理的权限过滤条件
     * （任一策略有数据且未忽略）
     */
    public static boolean hasPermissionFilter() {
        if (isIgnore()) {
            return false;
        }
        return !getOrgIds().isEmpty() || !getUserIds().isEmpty();
    }

    // ==================== 清理 ====================

    public static void clear() {
        ORG_IDS.remove();
        USER_IDS.remove();
        IGNORE.remove();
    }
}
