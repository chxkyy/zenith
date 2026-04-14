package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.dto.RolePageQuery;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import com.zenith.admin.infrastructure.dataobject.RoleDO;
import com.zenith.admin.infrastructure.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RoleConvertor roleConvertor;

    public MultiResponse<RoleDTO> listAll() {
        List<RoleDO> roleDOS = roleMapper.selectList(null);
        List<RoleEntity> entities = roleConvertor.toEntityList(roleDOS);
        List<RoleDTO> dtos = roleConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

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

        List<RoleEntity> entities = roleConvertor.toEntityList(roleDOS);
        List<RoleDTO> dtos = roleConvertor.toDTOList(entities);

        PageInfo<RoleDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    public void save(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        RoleDO roleDO = roleConvertor.toDataObject(entity);
        if (roleDO.getId() == null) {
            roleMapper.insert(roleDO);
        } else {
            roleMapper.updateById(roleDO);
        }
    }

    public void update(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        RoleEntity existingRole = getByIdEntity(roleDTO.getId());
        if (existingRole != null && "ADMIN".equals(existingRole.getCode())) {
            entity.setCode(existingRole.getCode());
        }
        RoleDO roleDO = roleConvertor.toDataObject(entity);
        roleMapper.updateById(roleDO);
    }

    public void delete(Long id) {
        RoleEntity role = getByIdEntity(id);
        if (role != null) {
            if ("ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可删除");
            }
            roleMapper.deleteById(id);
        }
    }

    public RoleDTO getById(Long id) {
        RoleEntity entity = getByIdEntity(id);
        return roleConvertor.toDTO(entity);
    }

    private RoleEntity getByIdEntity(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return roleConvertor.toEntity(roleDO);
    }

    public void changeStatus(Long id, Integer status) {
        RoleEntity role = getByIdEntity(id);
        if (role != null) {
            if (0 == status && "ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可禁用");
            }
            role.setStatus(status);
            RoleDO roleDO = roleConvertor.toDataObject(role);
            roleMapper.updateById(roleDO);
        }
    }
}
