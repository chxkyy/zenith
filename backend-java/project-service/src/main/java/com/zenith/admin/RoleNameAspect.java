package com.zenith.admin;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.annotation.RoleName;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RoleNameAspect {

    private final RoleMapper roleMapper;

    @PostConstruct
    public void init() {
        log.info("========== RoleNameAspect 初始化成功 ==========");
    }

    @AfterReturning(pointcut = "execution(* com.zenith.admin.service..*.*(..))", returning = "result")
    public void fillRoleNames(Object result) {
        log.info("[RoleNameAspect] fillRoleNames called, result type: {}", result != null ? result.getClass() : null);
        if (result == null) {
            log.warn("[RoleNameAspect] result is null, skipping");
            return;
        }
        try {
            log.info("[RoleNameAspect] Step 1: collecting role codes");
            Set<String> allRoleCodes = collectRoleCodes(result);
            log.info("[RoleNameAspect] Collected role codes: {}", allRoleCodes);

            log.info("[RoleNameAspect] Step 2: getting role code to name map");
            Map<String, String> roleCodeToNameMap = getRoleCodeToNameMap(allRoleCodes);
            log.info("[RoleNameAspect] Role code to name map: {}", roleCodeToNameMap);

            log.info("[RoleNameAspect] Step 3: processing result");
            processResult(result, roleCodeToNameMap);
            log.info("[RoleNameAspect] Processing completed");
        } catch (Exception e) {
            log.error("[RoleNameAspect] Failed to fill role names", e);
        }
    }

    private Map<String, String> getRoleCodeToNameMap(Collection<String> roleCodes) {
        log.info("[RoleNameAspect] getRoleCodeToNameMap called with roleCodes: {}", roleCodes);
        if (roleCodes == null || roleCodes.isEmpty()) {
            log.warn("[RoleNameAspect] roleCodes is null or empty");
            return Collections.emptyMap();
        }

        Set<String> uniqueCodes = new HashSet<>(roleCodes);
        log.info("[RoleNameAspect] Unique role codes: {}", uniqueCodes);

        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.in("code", uniqueCodes);
        log.info("[RoleNameAspect] Querying roles from database");
        List<RoleDO> roles = roleMapper.selectList(wrapper);
        log.info("[RoleNameAspect] Found {} roles from database", roles != null ? roles.size() : 0);

        Map<String, String> result = new HashMap<>();
        for (RoleDO role : roles) {
            String code = role.getCode();
            log.info("[RoleNameAspect] Processing role: code={}, name={}", code, role.getName());
            result.put(code, role.getName());
            if (code.startsWith("ROLE_")) {
                String shortCode = code.substring(5);
                result.put(shortCode, role.getName());
                log.info("[RoleNameAspect] Also added short code: {} -> {}", shortCode, role.getName());
            }
        }
        log.info("[RoleNameAspect] Returning role map: {}", result);
        return result;
    }

    private String getRoleName(String roleCode, Map<String, String> roleCodeToNameMap) {
        if (roleCode == null || roleCode.isEmpty()) {
            return "";
        }
        return roleCodeToNameMap.getOrDefault(roleCode, roleCode);
    }

    private String getRoleNames(String roleCodes, String separator, Map<String, String> roleCodeToNameMap) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return "";
        }
        String[] codes = roleCodes.split(separator);
        return Arrays.stream(codes)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .map(code -> getRoleName(code, roleCodeToNameMap))
                .collect(Collectors.joining(separator));
    }

    private Set<String> collectRoleCodes(Object result) throws Exception {
        Set<String> roleCodes = new HashSet<>();
        collectRoleCodesFromResult(result, roleCodes);
        return roleCodes;
    }

    private void collectRoleCodesFromResult(Object result, Set<String> roleCodes) throws Exception {
        if (result instanceof SingleResponse) {
            Object data = ((SingleResponse<?>) result).getData();
            if (data != null) {
                collectRoleCodesFromObject(data, roleCodes);
            }
        } else if (result instanceof MultiResponse) {
            List<?> data = ((MultiResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    collectRoleCodesFromObject(item, roleCodes);
                }
            }
        } else if (result instanceof PageResponse) {
            List<?> data = ((PageResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    collectRoleCodesFromObject(item, roleCodes);
                }
            }
        } else if (result instanceof PageInfo) {
            List<?> data = ((PageInfo<?>) result).getList();
            if (data != null) {
                for (Object item : data) {
                    collectRoleCodesFromObject(item, roleCodes);
                }
            }
        } else if (result instanceof Collection) {
            for (Object item : (Collection<?>) result) {
                collectRoleCodesFromObject(item, roleCodes);
            }
        } else {
            collectRoleCodesFromObject(result, roleCodes);
        }
    }

    private void collectRoleCodesFromObject(Object obj, Set<String> roleCodes) throws Exception {
        if (obj == null) {
            log.warn("[RoleNameAspect] collectRoleCodesFromObject: obj is null");
            return;
        }
        Class<?> clazz = obj.getClass();
        log.info("[RoleNameAspect] collectRoleCodesFromObject: class={}", clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        log.info("[RoleNameAspect] Found {} fields", fields != null ? fields.length : 0);
        for (Field field : fields) {
            log.info("[RoleNameAspect] Checking field: {}, has @RoleName: {}", field.getName(), field.isAnnotationPresent(RoleName.class));
            if (field.isAnnotationPresent(RoleName.class)) {
                RoleName annotation = field.getAnnotation(RoleName.class);
                String roleIdField = annotation.roleId();
                String separator = annotation.separator();
                log.info("[RoleNameAspect] Found @RoleName: roleIdField={}, separator={}", roleIdField, separator);

                Field sourceField;
                try {
                    sourceField = clazz.getDeclaredField(roleIdField);
                    log.info("[RoleNameAspect] Found source field: {}", roleIdField);
                } catch (NoSuchFieldException e) {
                    log.warn("[RoleNameAspect] Source field {} not found in {}", roleIdField, clazz.getName());
                    continue;
                }

                sourceField.setAccessible(true);
                Object roleValue = sourceField.get(obj);
                log.info("[RoleNameAspect] Source field value: {}", roleValue);
                if (roleValue != null) {
                    String roleStr = roleValue.toString();
                    log.info("[RoleNameAspect] Role string: {}", roleStr);
                    if (!roleStr.isEmpty()) {
                        String[] codes = roleStr.split(separator);
                        log.info("[RoleNameAspect] Split codes: {}", Arrays.toString(codes));
                        for (String code : codes) {
                            String trimmedCode = code.trim();
                            if (!trimmedCode.isEmpty()) {
                                roleCodes.add(trimmedCode);
                                log.info("[RoleNameAspect] Added role code: {}", trimmedCode);
                                if (trimmedCode.startsWith("ROLE_")) {
                                    roleCodes.add(trimmedCode.substring(5));
                                    log.info("[RoleNameAspect] Also added short code: {}", trimmedCode.substring(5));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void processResult(Object result, Map<String, String> roleCodeToNameMap) throws Exception {
        if (result instanceof SingleResponse) {
            Object data = ((SingleResponse<?>) result).getData();
            if (data != null) {
                processObject(data, roleCodeToNameMap);
            }
        } else if (result instanceof MultiResponse) {
            List<?> data = ((MultiResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    processObject(item, roleCodeToNameMap);
                }
            }
        } else if (result instanceof PageResponse) {
            List<?> data = ((PageResponse<?>) result).getData();
            if (data != null) {
                for (Object item : data) {
                    processObject(item, roleCodeToNameMap);
                }
            }
        } else if (result instanceof PageInfo) {
            List<?> data = ((PageInfo<?>) result).getList();
            if (data != null) {
                for (Object item : data) {
                    processObject(item, roleCodeToNameMap);
                }
            }
        } else if (result instanceof Collection) {
            for (Object item : (Collection<?>) result) {
                processObject(item, roleCodeToNameMap);
            }
        } else {
            processObject(result, roleCodeToNameMap);
        }
    }

    private void processObject(Object obj, Map<String, String> roleCodeToNameMap) throws Exception {
        if (obj == null) {
            log.warn("[RoleNameAspect] processObject: obj is null");
            return;
        }
        Class<?> clazz = obj.getClass();
        log.info("[RoleNameAspect] processObject: class={}", clazz.getName());
        Field[] fields = clazz.getDeclaredFields();
        log.info("[RoleNameAspect] Found {} fields", fields != null ? fields.length : 0);
        for (Field field : fields) {
            log.info("[RoleNameAspect] Checking field: {}, has @RoleName: {}", field.getName(), field.isAnnotationPresent(RoleName.class));
            if (field.isAnnotationPresent(RoleName.class)) {
                RoleName annotation = field.getAnnotation(RoleName.class);
                String roleIdField = annotation.roleId();
                String separator = annotation.separator();
                log.info("[RoleNameAspect] Found @RoleName: roleIdField={}, separator={}", roleIdField, separator);

                Field sourceField;
                try {
                    sourceField = clazz.getDeclaredField(roleIdField);
                    log.info("[RoleNameAspect] Found source field: {}", roleIdField);
                } catch (NoSuchFieldException e) {
                    log.warn("[RoleNameAspect] Source field {} not found in {}", roleIdField, clazz.getName());
                    continue;
                }

                sourceField.setAccessible(true);
                Object roleValue = sourceField.get(obj);
                log.info("[RoleNameAspect] Source field value: {}", roleValue);
                if (roleValue != null) {
                    String roleNames = getRoleNames(roleValue.toString(), separator, roleCodeToNameMap);
                    log.info("[RoleNameAspect] Generated roleNames: {}", roleNames);
                    field.setAccessible(true);
                    field.set(obj, roleNames);
                    log.info("[RoleNameAspect] Set field {} to value: {}", field.getName(), roleNames);
                }
            }
        }
    }
}
