package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.UserPageQuery;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import com.zenith.admin.infrastructure.dataobject.UserDO;
import com.zenith.admin.infrastructure.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGatewayImpl implements UserGateway {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserConvertor userConvertor;

    @Override
    public PageInfo<UserEntity> listByPage(UserPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("username", query.getKeyword())
                   .or().like("nickname", query.getKeyword())
                   .or().like("email", query.getKeyword());
        }
        
        // 部门筛选
        if (query.getOrgName() != null && !query.getOrgName().isEmpty()) {
            wrapper.eq("org_name", query.getOrgName());
        }
        
        // 角色筛选
        if (query.getRole() != null && !query.getRole().isEmpty()) {
            wrapper.eq("role", query.getRole());
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
        
        List<UserDO> userDOS = userMapper.selectList(wrapper);
        PageInfo<UserDO> pageInfo = new PageInfo<>(userDOS);
        
        List<UserEntity> entities = userConvertor.toEntityList(userDOS);

        PageInfo<UserEntity> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(entities);
        return result;
    }

    @Override
    public void save(UserEntity user) {
        UserDO userDO = userConvertor.toDataObject(user);
        if (userDO.getId() == null) {
            userMapper.insert(userDO);
        } else {
            userMapper.updateById(userDO);
        }
    }

    @Override
    public UserEntity getById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return userConvertor.toEntity(userDO);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}
