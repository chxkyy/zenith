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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleNameTranslateConverter implements FieldTranslateConverter {

    private final RoleMapper roleMapper;

    private final LoadingCache<String, String> roleCodeCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build(this::getRoleNameByCode);

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

        List<String> roleCodes;
        if (sourceValue instanceof Collection) {
            roleCodes = ((Collection<?>) sourceValue).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else {
            String sourceStr = String.valueOf(sourceValue);
            if (!StringUtils.hasText(sourceStr)) {
                return;
            }
            roleCodes = Arrays.stream(sourceStr.split(separator))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        }

        String roleNames = roleCodes.stream()
                .map(code -> {
                    String name = roleCodeCache.get(code);
                    if (name == null && code.startsWith("ROLE_")) {
                        name = roleCodeCache.get(code.substring(5));
                    }
                    return name != null ? name : code;
                })
                .collect(Collectors.joining(separator));

        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, roleNames);
    }

    private String getRoleNameByCode(String code) {
        Map<String, String> roleMap = new HashMap<>();
        List<RoleDO> roleList = roleMapper.selectList(new QueryWrapper<RoleDO>().in("code", code));
        for (RoleDO role : roleList) {
            roleMap.put(role.getCode(), role.getName());
            if (role.getCode().startsWith("ROLE_")) {
                roleMap.put(role.getCode().substring(5), role.getName());
            }
        }
        return roleMap.getOrDefault(code, code);
    }
}
