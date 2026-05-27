package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.RoleService;
import com.zenith.admin.dto.data.RoleAddCmd;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;
import com.zenith.admin.dto.data.RoleUpdateCmd;
import com.zenith.admin.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleConvertor roleConvertor;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    @Override
    public void changeStatus(Long id, Integer status, Long currentUserId) {
        if (SUPER_ADMIN_ROLE_ID.equals(id) && status == 0) {
            throw new RuntimeException("超级管理员角色不可禁用");
        }
        RoleDO role = roleMapper.selectById(id);
        if (role != null) {
            role.setStatus(status);
            role.setUpdateUserId(currentUserId);
            roleMapper.updateById(role);
        }
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        if (SUPER_ADMIN_ROLE_ID.equals(id)) {
            throw new RuntimeException("超级管理员角色不可删除");
        }
        
        QueryWrapper<UserRoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        Long userCount = userRoleMapper.selectCount(wrapper);
        if (userCount > 0) {
            throw new RuntimeException("该角色已分配给 " + userCount + " 个用户，请先移除用户关联");
        }
        
        roleMapper.deleteById(id);
    }

    @Override
    public RoleDTO getById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return roleConvertor.toDTO(roleDO);
    }

    @Override
    public List<RoleDTO> listActiveRoles() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("id");

        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        return roleConvertor.toDTOList(roleDOS);
    }

    @Override
    public List<RoleDTO> listAll() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        return roleConvertor.toDTOList(roleDOS);
    }

    @Override
    public PageInfo<RoleDTO> listByPage(RolePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("name", query.getKeyword());
        }

        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }

        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            String order = query.getSortOrder() != null && "desc".equals(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        }

        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        PageInfo<RoleDO> pageInfo = new PageInfo<>(roleDOS);

        List<RoleDTO> dtos = roleConvertor.toDTOList(roleDOS);

        PageInfo<RoleDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void save(RoleAddCmd cmd, Long currentUserId) {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("name", cmd.getName());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("角色名称已存在");
        }
        
        RoleDO roleDO = new RoleDO();
        roleDO.setName(cmd.getName());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleDO.setCreateUserId(currentUserId);
        roleDO.setUpdateUserId(currentUserId);
        roleMapper.insert(roleDO);
    }

    @Override
    public void update(RoleUpdateCmd cmd, Long currentUserId) {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("name", cmd.getName());
        wrapper.ne("id", cmd.getId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("角色名称已存在");
        }
        
        RoleDO roleDO = new RoleDO();
        roleDO.setId(cmd.getId());
        roleDO.setName(cmd.getName());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleDO.setUpdateUserId(currentUserId);
        roleMapper.updateById(roleDO);
    }
}
