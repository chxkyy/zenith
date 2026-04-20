package com.zenith.admin.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段翻译统一扩展接口
 * 新增业务翻译：实现该接口即可，无需要修改核心代码
 */
public interface FieldTranslateConverter {

    /**
     * 绑定当前转换器处理的注解
     */
    Class<? extends Annotation> supportAnnotation();

    /**
     * 字段翻译填充逻辑
     * @param dto 目标DTO对象
     * @param targetField 待赋值的目标字段
     */
    void translate(Object dto, Field targetField);
}