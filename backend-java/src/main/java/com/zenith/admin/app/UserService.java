package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import com.zenith.admin.infrastructure.dataobject.UserDO;
import com.zenith.admin.infrastructure.mapper.OrgMapper;
import com.zenith.admin.infrastructure.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private UserConvertor userConvertor;

    public PageResponse<UserDTO> listByPage(UserPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("username", query.getKeyword())
                   .or().like("nickname", query.getKeyword())
                   .or().like("email", query.getKeyword());
        }
        
        // 部门筛选（通过名称）
        if (query.getOrgName() != null && !query.getOrgName().isEmpty()) {
            wrapper.eq("org_name", query.getOrgName());
        }
        
        // 部门筛选（通过ID）
        if (query.getOrgId() != null) {
            OrgDO orgDO = orgMapper.selectById(query.getOrgId());
            if (orgDO != null) {
                wrapper.eq("org_name", orgDO.getName());
            }
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

        List<UserDTO> dtos = userConvertor.toDTOList(entities);

        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        UserDO userDO = userConvertor.toDataObject(entity);
        if (userDO.getId() == null) {
            userMapper.insert(userDO);
        } else {
            userMapper.updateById(userDO);
        }
    }

    public void update(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        UserDO userDO = userConvertor.toDataObject(entity);
        userMapper.updateById(userDO);
    }

    public void delete(Long id) {
        UserEntity user = getByIdEntity(id);
        if (user != null) {
            // 超级管理员保护：admin 用户不可删除
            if ("admin".equals(user.getUsername()) || "ADMIN".equals(user.getRole())) {
                throw new RuntimeException("超级管理员账号不可删除");
            }
            userMapper.deleteById(id);
        }
    }

    public UserDTO getById(Long id) {
        UserEntity entity = getByIdEntity(id);
        return userConvertor.toDTO(entity);
    }

    private UserEntity getByIdEntity(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return userConvertor.toEntity(userDO);
    }

    public void resetPassword(Long id) {
        // 实现密码重置逻辑
        UserEntity user = getByIdEntity(id);
        if (user != null) {
            // 这里可以添加密码重置逻辑，例如设置默认密码
            UserDO userDO = userConvertor.toDataObject(user);
            userMapper.updateById(userDO);
        }
    }

    public void changeStatus(Long id, Integer status) {
        // 实现状态切换逻辑
        UserEntity user = getByIdEntity(id);
        if (user != null) {
            // 超级管理员保护：admin 用户不可禁用
            if (0 == status && ("admin".equals(user.getUsername()) || "ADMIN".equals(user.getRole()))) {
                throw new RuntimeException("超级管理员账号不可禁用");
            }
            user.setStatus(status);
            UserDO userDO = userConvertor.toDataObject(user);
            userMapper.updateById(userDO);
        }
    }
}
