package com.zenith.admin.aspect;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.converter.FieldTranslateProcessor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class TranslateAspect {

    private final FieldTranslateProcessor fieldTranslateProcessor;

    /**
     * 切点：整个service包下所有方法，不限制方法名、无侵入
     */
    @Pointcut("execution(* com.zenith.admin.service..*(..))")
    public void servicePointCut() {
    }

    @AfterReturning(pointcut = "servicePointCut()", returning = "result")
    public void afterReturn(Object result) {
        if (result == null) {
            return;
        }
        // 1. 分页对象
        if (result instanceof PageInfo<?>) {
            PageInfo<?> pageInfo = (PageInfo<?>) result;
            pageInfo.getList().forEach(fieldTranslateProcessor::process);
        }
        // 2. 集合对象
        else if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            list.forEach(fieldTranslateProcessor::process);
        }
        // 3. 单个DTO对象
        else {
            fieldTranslateProcessor.process(result);
        }
    }
}