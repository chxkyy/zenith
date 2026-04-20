package com.zenith.admin.converter;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TranslateConverterRegistry {

    private final Map<Class<? extends Annotation>, FieldTranslateConverter> converterMap;

    /**
     * 自动注入所有实现类，完成注解与转换器绑定
     */
    public TranslateConverterRegistry(List<FieldTranslateConverter> converterList) {
        converterMap = new HashMap<>(16);
        if (CollectionUtils.isEmpty(converterList)) {
            return;
        }
        for (FieldTranslateConverter converter : converterList) {
            converterMap.put(converter.supportAnnotation(), converter);
        }
    }

    /**
     * 根据注解类型获取对应转换器
     */
    public FieldTranslateConverter getConverter(Class<? extends Annotation> annoClass) {
        return converterMap.get(annoClass);
    }
}