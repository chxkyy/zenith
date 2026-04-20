package com.zenith.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DictTranslate {
    /** 关联原始字典码字段 */
    String source();
    /** 字典类型标识 */
    String dictType();
    /** 多值分隔符 */
    String separator() default ",";
}