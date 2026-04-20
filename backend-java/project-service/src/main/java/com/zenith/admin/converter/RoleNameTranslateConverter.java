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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleNameTranslateConverter implements FieldTranslateConverter {

    private final RoleMapper roleMapper;

    // 全局角色缓存
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

        // 获取原始角色编码字段
        Field sourceField = ReflectionUtils.findField(dto.getClass(), sourceFieldName);
        if (sourceField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(sourceField);
        Object sourceValue = ReflectionUtils.getField(sourceField, dto);
        if (!StringUtils.hasText(String.valueOf(sourceValue))) {
            return;
        }

        // 多值拆分翻译
        String roleNames = Arrays.stream(String.valueOf(sourceValue).split(separator))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(code -> {
                    String name = roleCodeCache.get(code);
                    // 兼容 ROLE_ 短编码
                    if (name == null && code.startsWith("ROLE_")) {
                        name = roleCodeCache.get(code.substring(5));
                    }
                    return name != null ? name : code;
                })
                .collect(Collectors.joining(separator));

        // 赋值到目标字段
        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, roleNames);
    }

    /**
     * 批量查询角色（合并查询，避免N+1）
     */
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