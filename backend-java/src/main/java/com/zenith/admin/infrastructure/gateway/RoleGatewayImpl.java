package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.RoleGateway;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RolePageQuery;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import com.zenith.admin.infrastructure.dataobject.RoleDO;
import com.zenith.admin.infrastructure.mapper.RoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleGatewayImpl implements RoleGateway {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleConvertor roleConvertor;

    @Override
    public List<RoleEntity> listAll() {
        List<RoleDO> roleDOS = roleMapper.selectList(null);
        return roleConvertor.toEntityList(roleDOS);
    }

    @Override
    public PageInfo<RoleEntity> listByPage(RolePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("name", query.getKeyword())
                   .or().like("code", query.getKeyword());
        }
        
        // 状态筛选
        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        
        // 排序
        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            String order = query.getSortOrder() != null && "desc".equals(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        }
        
        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        PageInfo<RoleDO> pageInfo = new PageInfo<>(roleDOS);
        
        List<RoleEntity> entities = roleConvertor.toEntityList(roleDOS);

        PageInfo<RoleEntity> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(entities);
        return result;
    }

    @Override
    public void save(RoleEntity role) {
        RoleDO roleDO = roleConvertor.toDataObject(role);
        if (roleDO.getId() == null) {
            roleMapper.insert(roleDO);
        } else {
            roleMapper.updateById(roleDO);
        }
    }

    @Override
    public RoleEntity getById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return roleConvertor.toEntity(roleDO);
    }

    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }
}
