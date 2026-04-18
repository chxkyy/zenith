package com.zenith.admin;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.service.RoleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RoleNameAspect {

    private final RoleCacheService roleCacheService;

    @AfterReturning(pointcut = "execution(* com.zenith.admin.service..*.*(..))", returning = "result")
    public void fillRoleNames(Object result) {
        if (result == null) {
            return;
        }
        try {
            processResult(result);
        } catch (Exception e) {
            log.warn("Failed to fill role names", e);
        }
    }

    private void processResult(Object result) throws Exception {
        if (result instanceof SingleResponse) {
            Object data = ((SingleResponse<?>) result).getData();
            if (data != null) {
                processObject(data);
            }
        } else if (result instanceof MultiResponse) {
            List<?> data = ((MultiResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    processObject(item);
                }
            }
        } else if (result instanceof PageResponse) {
            List<?> data = ((PageResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    processObject(item);
                }
            }
        } else if (result instanceof PageInfo) {
            List<?> data = ((PageInfo<?>) result).getList();
            if (data != null) {
                for (Object item : data) {
                    processObject(item);
                }
            }
        } else if (result instanceof Collection) {
            for (Object item : (Collection<?>) result) {
                processObject(item);
            }
        } else {
            processObject(result);
        }
    }

    private void processObject(Object obj) throws Exception {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RoleName.class)) {
                RoleName annotation = field.getAnnotation(RoleName.class);
                String roleIdField = annotation.roleId();
                String separator = annotation.separator();

                Field sourceField;
                try {
                    sourceField = clazz.getDeclaredField(roleIdField);
                } catch (NoSuchFieldException e) {
                    log.warn("Source field {} not found in {}", roleIdField, clazz.getName());
                    continue;
                }

                sourceField.setAccessible(true);
                Object roleValue = sourceField.get(obj);
                if (roleValue != null) {
                    String roleNames = roleCacheService.getRoleNames(roleValue.toString(), separator);
                    field.setAccessible(true);
                    field.set(obj, roleNames);
                }
            }
        }
    }
}
