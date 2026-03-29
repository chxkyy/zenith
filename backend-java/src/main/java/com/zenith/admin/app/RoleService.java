package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.RoleGateway;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.dto.RolePageQuery;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleGateway roleGateway;

    @Autowired
    private RoleConvertor roleConvertor;

    public MultiResponse<RoleDTO> listAll() {
        List<RoleEntity> entities = roleGateway.listAll();
        List<RoleDTO> dtos = roleConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public PageResponse<RoleDTO> listByPage(RolePageQuery query) {
        PageInfo<RoleEntity> pageInfo = roleGateway.listByPage(query);
        List<RoleDTO> dtos = roleConvertor.toDTOList(pageInfo.getList());
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        roleGateway.save(entity);
    }

    public void update(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        // 超级管理员角色保护：ADMIN 角色编码不可修改
        RoleEntity existingRole = roleGateway.getById(roleDTO.getId());
        if (existingRole != null && "ADMIN".equals(existingRole.getCode())) {
            entity.setCode(existingRole.getCode()); // 保持原编码不变
        }
        roleGateway.save(entity);
    }

    public void delete(Long id) {
        RoleEntity role = roleGateway.getById(id);
        if (role != null) {
            // 超级管理员角色保护：ADMIN 角色不可删除
            if ("ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可删除");
            }
            roleGateway.deleteById(id);
        }
    }

    public RoleDTO getById(Long id) {
        RoleEntity entity = roleGateway.getById(id);
        return roleConvertor.toDTO(entity);
    }

    public void changeStatus(Long id, Integer status) {
        RoleEntity role = roleGateway.getById(id);
        if (role != null) {
            // 超级管理员角色保护：ADMIN 角色不可禁用
            if (0 == status && "ADMIN".equals(role.getCode())) {
                throw new RuntimeException("超级管理员角色不可禁用");
            }
            role.setStatus(status);
            roleGateway.save(role);
        }
    }
}
