package com.zenith.admin.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据权限 SQL 改写拦截器（MyBatis-Plus InnerInterceptor）
 * <p>
 * 在 SQL 执行前，读取 {@link DataPermissionHelper} 中的权限范围数据，
 * 通过字符串操作自动改写 SQL 追加权限过滤条件。对上层业务代码完全透明。
 * </p>
 *
 * <h3>改写规则：</h3>
 * <ul>
 *   <li><b>策略一（ORG）</b>：追加 {@code AND org_id IN (orgId1, orgId2, ...)} 条件</li>
 *   <li><b>策略二（OWNER_ORG）</b>：在 FROM 子句后添加 INNER JOIN t_data_permission +
 *       在 WHERE 子句中追加 {@code dp.user_id IN (userId1, userId2, ...)} 条件</li>
 * </ul>
 *
 * @see DataPermissionHelper
 * @see com.zenith.admin.annotation.DataPermission
 */
@Slf4j
@Component
public class DataPermissionInnerInterceptor implements InnerInterceptor {

    /** 策略二 JOIN 绑定表的别名 */
    private static final String DP_ALIAS = "dp";

    /** 匹配 ORDER BY / GROUP BY / LIMIT 等子句（用于定位 WHERE 注入点） */
    private static final Pattern TAIL_CLAUSE_PATTERN =
            Pattern.compile("\\s+(ORDER\\s+BY|GROUP\\s+BY|LIMIT|OFFSET)\\s", Pattern.CASE_INSENSITIVE);

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        if (!DataPermissionHelper.hasPermissionFilter()) {
            return;
        }

        String originalSql = boundSql.getSql();
        String rewrittenSql = rewriteSql(originalSql);

        if (rewrittenSql != null && !rewrittenSql.equals(originalSql)) {
            try {
                var field = BoundSql.class.getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, rewrittenSql);
                log.debug("数据权限 SQL 改写完成:\n原始: {}\n改写: {}", originalSql, rewrittenSql);
            } catch (Exception e) {
                log.error("数据权限 SQL 改写失败", e);
            }
        }
    }

    /**
     * 改写 SQL，追加数据权限过滤条件
     */
    private String rewriteSql(String originalSql) {
        try {
            List<Long> orgIds = DataPermissionHelper.getOrgIds();
            List<Long> userIds = DataPermissionHelper.getUserIds();

            // 策略二优先：如果存在用户ID列表，使用 OWNER_ORG 策略（JOIN 绑定表）
            if (!userIds.isEmpty()) {
                return applyOwnerOrgFilter(originalSql, userIds);
            }
            // 策略一：如果存在组织ID列表，使用 ORG 策略
            if (!orgIds.isEmpty()) {
                return applyOrgFilter(originalSql, orgIds);
            }

            return null;
        } catch (Exception e) {
            log.warn("SQL 改写失败，跳过数据权限过滤: {}", originalSql, e);
            return null;
        }
    }

    /**
     * 策略一（ORG）：在 WHERE 子句后追加 org_id IN (...) 条件
     *
     * <p>处理逻辑：</p>
     * <ol>
     *   <li>查找 SQL 中是否有 WHERE 关键字</li>
     *   <li>有 → 在 ORDER BY / GROUP BY / LIMIT 之前注入 AND 条件</li>
     *   <li>无 → 在第一个 ORDER BY / GROUP BY / LIMIT / 尾部之前注入 WHERE 条件</li>
     * </ol>
     */
    private String applyOrgFilter(String sql, List<Long> orgIds) {
        String condition = "org_id IN (" + joinIds(orgIds) + ")";
        return injectWhereCondition(sql, condition);
    }

    /**
     * 策略二（OWNER_ORG）：添加 INNER JOIN + user_id 过滤条件
     *
     * <p>处理逻辑：</p>
     * <ol>
     *   <li>找到 FROM 子句后的位置，插入 INNER JOIN</li>
     *   <li>在 WHERE 子句后追加 dp.user_id IN (...) 条件</li>
     * </ol>
     */
    private String applyOwnerOrgFilter(String sql, List<Long> userIds) {
        // 1. 添加 INNER JOIN（在 FROM 主表之后、WHERE 之前）
        String joinClause = " INNER JOIN t_data_permission " + DP_ALIAS
                + " ON " + DP_ALIAS + ".data_id = {mainTable}.id";
        sql = insertAfterFrom(sql, joinClause);

        // 2. 追加 WHERE 条件
        String condition = DP_ALIAS + ".user_id IN (" + joinIds(userIds) + ")";
        return injectWhereCondition(sql, condition);
    }

    /**
     * 在 FROM 子句之后插入 JOIN 子句
     *
     * <p>查找 FROM 和下一个关键字（WHERE / ORDER BY / GROUP BY / LIMIT）之间的位置。</p>
     */
    private String insertAfterFrom(String sql, String joinClause) {
        // 将 SQL 转为大写用于匹配，保留原始大小写
        String upperSql = sql.toUpperCase();
        int fromIndex = upperSql.indexOf(" FROM ");
        if (fromIndex == -1) {
            log.warn("未找到 FROM 子句，跳过 JOIN 注入");
            return sql;
        }

        // 从 FROM 之后开始找下一个关键字的位置
        int searchStart = fromIndex + 6; // 跳过 " FROM "
        int insertPos = findNextKeywordPosition(upperSql, searchStart);

        if (insertPos == -1) {
            // 没有其他子句，直接在末尾追加
            return sql + joinClause;
        }

        return sql.substring(0, insertPos) + joinClause + sql.substring(insertPos);
    }

    /**
     * 在 SQL 的合适位置注入 WHERE 过滤条件
     */
    private String injectWhereCondition(String sql, String condition) {
        String upperSql = sql.toUpperCase();

        // 查找已有 WHERE 子句
        int whereIndex = findWherePosition(upperSql);

        if (whereIndex != -1) {
            // 已有 WHERE → 在其后（ORDER BY / GROUP BY / LIMIT 之前）追加 AND 条件
            int afterWhere = whereIndex + 6; // 跳过 "WHERE"
            int tailPos = findTailClausePosition(upperSql, afterWhere);

            if (tailPos != -1) {
                return sql.substring(0, tailPos)
                        + " AND (" + condition + ")"
                        + sql.substring(tailPos);
            } else {
                // 没有 ORDER BY 等，直接追加到末尾
                return sql + " AND (" + condition + ")";
            }
        } else {
            // 没有 WHERE → 需要插入 WHERE 子句
            int tailPos = findTailClausePosition(upperSql, 0);
            if (tailPos != -1) {
                return sql.substring(0, tailPos)
                        + " WHERE " + condition
                        + sql.substring(tailPos);
            } else {
                return sql + " WHERE " + condition;
            }
        }
    }

    /**
     * 查找 WHERE 关键字位置（不在字符串内的）
     */
    private int findWherePosition(String upperSql) {
        int idx = 0;
        while ((idx = upperSql.indexOf(" WHERE ", idx)) != -1) {
            // 简单检查：确保不是在子查询或字符串内（基本防护）
            if (!isInsideSubqueryOrString(upperSql, idx)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    /**
     * 查找尾部子句（ORDER BY / GROUP BY / LIMIT / OFFSET）的位置
     */
    private int findTailClausePosition(String upperSql, int startFrom) {
        Matcher matcher = TAIL_CLAUSE_PATTERN.matcher(upperSql);
        while (matcher.find()) {
            int pos = matcher.start();
            if (pos >= startFrom && !isInsideSubqueryOrString(upperSql, pos)) {
                return pos;
            }
        }
        return -1;
    }

    /**
     * 从指定位置开始查找下一个 SQL 关键字的位置
     */
    private int findNextKeywordPosition(String upperSql, int startFrom) {
        String[] keywords = {" WHERE ", " ORDER BY ", " GROUP BY ", " LIMIT ", " OFFSET ",
                             " LEFT JOIN ", " RIGHT JOIN ", " INNER JOIN ", " JOIN "};
        int firstPos = Integer.MAX_VALUE;

        for (String keyword : keywords) {
            int pos = upperSql.indexOf(keyword, startFrom);
            if (pos != -1 && pos >= startFrom && !isInsideSubqueryOrString(upperSql, pos)) {
                firstPos = Math.min(firstPos, pos);
            }
        }

        return firstPos == Integer.MAX_VALUE ? -1 : firstPos;
    }

    /**
     * 基本检查：判断位置是否可能在子查询或字符串内部
     * （简化实现，覆盖常见场景）
     */
    private boolean isInsideSubqueryOrString(String sql, int pos) {
        // 统计此位置之前的括号深度和引号状态
        int parenDepth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < pos && i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'' && !inDoubleQuote) inSingleQuote = !inSingleQuote;
            else if (c == '"' && !inSingleQuote) inDoubleQuote = !inDoubleQuote;
            else if (!inSingleQuote && !inDoubleQuote) {
                if (c == '(') parenDepth++;
                else if (c == ')') parenDepth--;
            }
        }

        return parenDepth > 0 || inSingleQuote || inDoubleQuote;
    }

    /**
     * 将 ID 列表拼接为逗号分隔的字符串
     */
    private String joinIds(List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(ids.get(i));
        }
        return sb.toString();
    }
}
