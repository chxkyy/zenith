package com.zenith.admin.converter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.zenith.admin.annotation.OrgName;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 组织名称自动翻译转换器
 * 
 * 功能：
 * 根据 @OrgName 注解，自动将组织ID翻译为组织名称
 * 
 * 使用场景：
 * UserDTO 等需要展示组织名称的DTO对象
 * 
 * 性能优化：
 * - 使用 Caffeine 本地缓存
 * - 缓存过期时间：10分钟
 * - 最大缓存条数：500条
 * 
 * 示例：
 * <pre>
 * // DTO定义
 * public class UserDTO {
 *     private Long orgId;  // 组织ID
 *     
 *     &#64;OrgName(orgId = "orgId")
 *     private String orgName;  // 自动填充：根据orgId查询组织名称
 * }
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class OrgNameTranslateConverter implements FieldTranslateConverter {

    private final OrgMapper orgMapper;

    /**
     * Caffeine 缓存配置
     * - 过期时间：10分钟（写入后）
     * - 最大容量：500条记录
     * - 淘汰策略：LRU（最近最少使用）
     */
    private final LoadingCache<Long, String> orgNameCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build(this::getOrgNameById);

    /**
     * 支持的注解类型：@OrgName
     */
    @Override
    public Class<? extends Annotation> supportAnnotation() {
        return OrgName.class;
    }

    /**
     * 执行字段翻译逻辑
     * 
     * @param dto 目标DTO对象
     * @param targetField 待填充的目标字段（标注了 @OrgName 注解）
     */
    @Override
    public void translate(Object dto, Field targetField) {
        // 1. 获取注解实例
        OrgName orgNameAnno = targetField.getAnnotation(OrgName.class);
        if (orgNameAnno == null) {
            return;
        }

        // 2. 获取源字段名（orgId）
        String sourceFieldName = orgNameAnno.orgId();

        // 3. 查找源字段对象
        Field sourceField = ReflectionUtils.findField(dto.getClass(), sourceFieldName);
        if (sourceField == null) {
            return;
        }

        // 4. 设置可访问性并获取源值（orgId）
        ReflectionUtils.makeAccessible(sourceField);
        Object sourceValue = ReflectionUtils.getField(sourceField, dto);
        if (sourceValue == null) {
            return;
        }

        // 5. 将源值转换为 Long 类型
        Long orgId;
        try {
            orgId = Long.valueOf(String.valueOf(sourceValue));
        } catch (NumberFormatException e) {
            // 如果 orgId 不是有效的数字，跳过翻译
            return;
        }

        // 6. 从缓存或数据库获取组织名称
        String orgName = orgNameCache.get(orgId);
        if (orgName == null) {
            return;
        }

        // 7. 设置目标字段的值（orgName）
        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, orgName);
    }

    /**
     * 根据组织ID查询组织名称
     * 
     * 此方法会被 Caffeine 缓存自动调用
     * 当缓存未命中时执行此方法加载最新数据
     * 
     * @param orgId 组织ID
     * @return 组织名称，如果不存在返回null
     */
    private String getOrgNameById(Long orgId) {
        OrgDO orgDO = orgMapper.selectById(orgId);
        return orgDO != null ? orgDO.getName() : null;
    }
}
