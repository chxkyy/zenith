package com.zenith.admin.converter;

import com.zenith.admin.annotation.DictTranslate;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.mapper.DictItemMapper;
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
public class DictTranslateConverter implements FieldTranslateConverter {

    private final DictItemMapper dictItemMapper;

    // 全局字典缓存
    private final LoadingCache<String, Map<String, String>> dictCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)
            .build(this::loadDictMap);

    @Override
    public Class<? extends Annotation> supportAnnotation() {
        return DictTranslate.class;
    }

    @Override
    public void translate(Object dto, Field targetField) {
        DictTranslate dictTranslateAnno = targetField.getAnnotation(DictTranslate.class);
        String sourceFieldName = dictTranslateAnno.source();
        String dictType = dictTranslateAnno.dictType();
        String separator = dictTranslateAnno.separator();

        // 获取原始字典码字段
        Field sourceField = ReflectionUtils.findField(dto.getClass(), sourceFieldName);
        if (sourceField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(sourceField);
        Object sourceValue = ReflectionUtils.getField(sourceField, dto);
        if (!StringUtils.hasText(String.valueOf(sourceValue))) {
            return;
        }

        // 获取字典映射
        Map<String, String> dictMap = dictCache.get(dictType);

        // 多值拆分翻译
        String dictNames = Arrays.stream(String.valueOf(sourceValue).split(separator))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(code -> dictMap.getOrDefault(code, code))
                .collect(Collectors.joining(separator));

        // 赋值到目标字段
        ReflectionUtils.makeAccessible(targetField);
        ReflectionUtils.setField(targetField, dto, dictNames);
    }

    /**
     * 加载字典映射
     */
    private Map<String, String> loadDictMap(String dictType) {
        Map<String, String> dictMap = new HashMap<>();
        List<DictItemDO> dictItems = dictItemMapper.selectList(new QueryWrapper<DictItemDO>().eq("type", dictType));
        for (DictItemDO item : dictItems) {
            dictMap.put(item.getDictValue(), item.getLabel());
        }
        return dictMap;
    }
}