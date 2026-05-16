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
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleConvertor roleConvertor;
    private final RoleMapper roleMapper;

    @Override
    public void changeStatus(Long id, Integer status, Long currentUserId) {
        RoleDO role = roleMapper.selectById(id);
        if (role != null) {
            if (0 == status && "ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可禁用");
            }
            role.setStatus(status);
            roleMapper.updateById(role);
        }
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        RoleDO role = roleMapper.selectById(id);
        if (role != null) {
            if ("ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可删除");
            }
            roleMapper.deleteById(id);
        }
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

        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        return roleConvertor.toDTOList(roleDOS);
    }

    @Override
    public List<RoleDTO> listAll() {
        List<RoleDO> roleDOS = roleMapper.selectList(null);
        return roleConvertor.toDTOList(roleDOS);
    }

    @Override
    public PageInfo<RoleDTO> listByPage(RolePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("name", query.getKeyword())
                    .or().like("code", query.getKeyword());
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
        RoleDO roleDO = new RoleDO();
        roleDO.setName(cmd.getName());
        roleDO.setCode(cmd.getCode());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleMapper.insert(roleDO);
    }

    @Override
    public void update(RoleUpdateCmd cmd, Long currentUserId) {
        RoleDO existingRole = roleMapper.selectById(cmd.getId());
        if (existingRole != null && "ADMIN".equals(existingRole.getCode())) {
            cmd.setCode(existingRole.getCode());
        }

        RoleDO roleDO = new RoleDO();
        roleDO.setId(cmd.getId());
        roleDO.setName(cmd.getName());
        roleDO.setCode(cmd.getCode());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleMapper.updateById(roleDO);
    }
}
