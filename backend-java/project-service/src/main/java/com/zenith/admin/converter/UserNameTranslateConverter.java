package com.zenith.admin.converter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.zenith.admin.annotation.UserName;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserNameTranslateConverter implements FieldTranslateConverter {

    private final UserMapper userMapper;

    private final LoadingCache<Long, String> userNameCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build(this::getUserNameById);

    @Override
    public Class<? extends Annotation> supportAnnotation() {
        return UserName.class;
    }

    @Override
    public void translate(Object dto, Field targetField) {
        UserName userNameAnno = targetField.getAnnotation(UserName.class);
        String sourceFieldName = userNameAnno.userId();

        Field sourceField = ReflectionUtils.findField(dto.getClass(), sourceFieldName);
        if (sourceField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(sourceField);
        Object sourceValue = ReflectionUtils.getField(sourceField, dto);
        if (sourceValue == null) {
            return;
        }

        Long userId;
        try {
            userId = Long.valueOf(String.valueOf(sourceValue));
        } catch (NumberFormatException e) {
            return;
        }

        String username = userNameCache.get(userId);
        if (username == null) {
            return;
        }

        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, username);
    }

    private String getUserNameById(Long userId) {
        UserDO userDO = userMapper.selectById(userId);
        return userDO != null ? userDO.getUsername() : null;
    }
}
