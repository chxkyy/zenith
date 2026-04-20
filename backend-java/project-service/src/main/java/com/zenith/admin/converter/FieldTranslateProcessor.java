package com.zenith.admin.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Slf4j
@Component
@RequiredArgsConstructor
public class FieldTranslateProcessor {

    private final TranslateConverterRegistry registry;

    /**
     * 统一入口：填充对象所有翻译字段
     * 支持单个DTO / 嵌套对象(简易扩展)
     */
    public void process(Object dto) {
        if (dto == null) {
            return;
        }
        // 遍历所有字段
        ReflectionUtils.doWithFields(dto.getClass(), field -> {
            // 遍历字段上所有注解
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                FieldTranslateConverter converter = registry.getConverter(annotation.annotationType());
                if (converter == null) {
                    continue;
                }
                try {
                    converter.translate(dto, field);
                } catch (Exception e) {
                    log.error("[字段翻译失败] 类:{},字段:{}", dto.getClass().getSimpleName(), field.getName(), e);
                }
            }
        });
    }
}