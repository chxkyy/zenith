package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.RoleService;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;
import com.zenith.admin.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleConvertor roleConvertor;

    @Override
    public List<RoleDTO> listAll() {
        List<RoleDO> roleDOS = roleMapper.selectList(null);
        List<RoleDTO> dtos = roleConvertor.toDTOList(roleDOS);
        return dtos;
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
    public void save(RoleDTO roleDTO) {
        RoleDO roleDO = roleConvertor.toDataObject(roleDTO);
        Long currentUserId = 1L;
        if (roleDO.getId() == null) {
            roleDO.setCreateUserId(currentUserId);
            roleDO.setCreatedTime(java.time.LocalDateTime.now());
            roleMapper.insert(roleDO);
        } else {
            roleDO.setUpdateUserId(currentUserId);
            roleDO.setUpdateTime(java.time.LocalDateTime.now());
            roleMapper.updateById(roleDO);
        }
    }

    @Override
    public void update(RoleDTO roleDTO) {
        RoleDO existingRole = roleMapper.selectById(roleDTO.getId());
        if (existingRole != null && "ADMIN".equals(existingRole.getCode())) {
            roleDTO.setCode(existingRole.getCode());
        }
        RoleDO roleDO = roleConvertor.toDataObject(roleDTO);
        Long currentUserId = 1L;
        roleDO.setUpdateUserId(currentUserId);
        roleDO.setUpdateTime(java.time.LocalDateTime.now());
        roleMapper.updateById(roleDO);
    }

    @Override
    public void delete(Long id) {
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
    public void changeStatus(Long id, Integer status) {
        RoleDO role = roleMapper.selectById(id);
        if (role != null) {
            if (0 == status && "ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可禁用");
            }
            role.setStatus(status);
            Long currentUserId = 1L;
            role.setUpdateUserId(currentUserId);
            role.setUpdateTime(java.time.LocalDateTime.now());
            roleMapper.updateById(role);
        }
    }
}
