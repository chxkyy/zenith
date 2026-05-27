package com.zenith.admin.converter;

import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleNameTranslateConverter implements FieldTranslateConverter {

    private final RoleMapper roleMapper;

    private final LoadingCache<Long, String> roleIdCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build(this::getRoleNameById);

    @Override
    public Class<? extends Annotation> supportAnnotation() {
        return RoleName.class;
    }

    @Override
    public void translate(Object dto, Field targetField) {
        RoleName roleNameAnno = targetField.getAnnotation(RoleName.class);
        String sourceFieldName = roleNameAnno.roleId();
        String separator = roleNameAnno.separator();

        Field sourceField = ReflectionUtils.findField(dto.getClass(), sourceFieldName);
        if (sourceField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(sourceField);
        Object sourceValue = ReflectionUtils.getField(sourceField, dto);
        if (sourceValue == null) {
            return;
        }

        List<Long> roleIds;
        if (sourceValue instanceof Collection) {
            roleIds = ((Collection<?>) sourceValue).stream()
                    .map(v -> {
                        if (v instanceof Long) return (Long) v;
                        if (v instanceof Number) return ((Number) v).longValue();
                        try {
                            return Long.parseLong(String.valueOf(v));
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
        } else {
            String sourceStr = String.valueOf(sourceValue);
            if (!StringUtils.hasText(sourceStr)) {
                return;
            }
            roleIds = Arrays.stream(sourceStr.split(separator))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(s -> {
                        try {
                            return Long.parseLong(s);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
        }

        String roleNames = roleIds.stream()
                .map(roleId -> {
                    String name = roleIdCache.get(roleId);
                    return name != null ? name : "未知角色";
                })
                .collect(Collectors.joining(separator));

        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, roleNames);
    }

    private String getRoleNameById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        RoleDO role = roleMapper.selectById(roleId);
        return role != null ? role.getName() : null;
    }
}
