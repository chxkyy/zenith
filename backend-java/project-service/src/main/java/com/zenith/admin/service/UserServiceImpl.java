package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.UserService;
import com.zenith.admin.UserConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final OrgMapper orgMapper;
    private final UserConvertor userConvertor;

    @Override
    public PageInfo<UserDTO> listByPage(UserPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("username", query.getKeyword())
                   .or().like("email", query.getKeyword());
        }

        if (query.getOrgName() != null && !query.getOrgName().isEmpty()) {
            wrapper.eq("org_name", query.getOrgName());
        }

        if (query.getOrgId() != null) {
            OrgDO orgDO = orgMapper.selectById(query.getOrgId());
            if (orgDO != null) {
                wrapper.eq("org_name", orgDO.getName());
            }
        }

        if (query.getRole() != null && !query.getRole().isEmpty()) {
            wrapper.eq("role", query.getRole());
        }

        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }

        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            String order = query.getSortOrder() != null && "desc".equals(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        }

        List<UserDO> userDOS = userMapper.selectList(wrapper);
        PageInfo<UserDO> pageInfo = new PageInfo<>(userDOS);

        List<UserDTO> dtos = userConvertor.toDTOList(userDOS);

        PageInfo<UserDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void save(UserDTO userDTO) {
        UserDO userDO = userConvertor.toDataObject(userDTO);
        Long currentUserId = 1L; // TODO: 从上下文获取当前登录用户ID
        if (userDO.getId() == null) {
            userDO.setCreateUserId(currentUserId);
            userDO.setCreatedTime(java.time.LocalDateTime.now());
            userMapper.insert(userDO);
        } else {
            userDO.setUpdateUserId(currentUserId);
            userDO.setUpdateTime(java.time.LocalDateTime.now());
            userMapper.updateById(userDO);
        }
    }

    @Override
    public void update(UserDTO userDTO) {
        UserDO userDO = userConvertor.toDataObject(userDTO);
        Long currentUserId = 1L; // TODO: 从上下文获取当前登录用户ID
        userDO.setUpdateUserId(currentUserId);
        userDO.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.updateById(userDO);
    }

    @Override
    public void delete(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if ("admin".equals(userDO.getUsername()) || "ADMIN".equals(userDO.getRole())) {
                throw new RuntimeException("超级管理员账号不可删除");
            }
            userMapper.deleteById(id);
        }
    }

    @Override
    public UserDTO getById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return userConvertor.toDTO(userDO);
    }

    @Override
    public void resetPassword(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            userMapper.updateById(userDO);
        }
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if (0 == status && ("admin".equals(userDO.getUsername()) || "ADMIN".equals(userDO.getRole()))) {
                throw new RuntimeException("超级管理员账号不可禁用");
            }
            userDO.setStatus(status);
            Long currentUserId = 1L; // TODO: 从上下文获取当前登录用户ID
            userDO.setUpdateUserId(currentUserId);
            userDO.setUpdateTime(java.time.LocalDateTime.now());
            userMapper.updateById(userDO);
        }
    }
}
