package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenith.admin.service.system.executor.converter.UserConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserGetByLoginIdQryExe {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final UserConvertor userConvertor;

    public UserDTO execute(String loginId) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getLoginId, loginId);
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            return null;
        }
        List<UserDTO> dtos = convertToDTOsWithRoles(Collections.singletonList(userDO));
        return dtos.isEmpty() ? null : dtos.get(0);
    }

    private List<UserDTO> convertToDTOsWithRoles(List<UserDO> userDOS) {
        if (userDOS == null || userDOS.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = userDOS.stream()
                .map(UserDO::getId)
                .collect(Collectors.toList());

        Map<Long, List<Long>> userRoleIdsMap = getUserRoleIdsMap(userIds);
        Map<Long, String> roleIdToNameMap = getRoleIdToNameMap();

        return userDOS.stream().map(userDO -> {
            UserDTO dto = userConvertor.toDTO(userDO);

            List<Long> roleIds = userRoleIdsMap.getOrDefault(userDO.getId(), Collections.emptyList());
            List<String> roleNames = roleIds.stream()
                    .map(roleId -> roleIdToNameMap.getOrDefault(roleId, "未知角色"))
                    .collect(Collectors.toList());
            
            dto.setRoles(roleIds.stream().map(String::valueOf).collect(Collectors.toList()));
            dto.setRoleNames(String.join(", ", roleNames));

            return dto;
        }).collect(Collectors.toList());
    }

    private Map<Long, List<Long>> getUserRoleIdsMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRoleDO::getUserId, userIds);

        List<UserRoleDO> userRoles = userRoleMapper.selectList(wrapper);

        return userRoles.stream()
                .collect(Collectors.groupingBy(
                        UserRoleDO::getUserId,
                        Collectors.mapping(UserRoleDO::getRoleId, Collectors.toList())
                ));
    }

    private Map<Long, String> getRoleIdToNameMap() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        List<RoleDO> roles = roleMapper.selectList(wrapper);
        return roles.stream()
                .collect(Collectors.toMap(RoleDO::getId, RoleDO::getName));
    }
}
