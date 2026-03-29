package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserGateway userGateway;

    @Autowired
    private UserConvertor userConvertor;

    public PageResponse<UserDTO> listByPage(UserPageQuery query) {
        PageInfo<UserEntity> pageInfo = userGateway.listByPage(query);
        
        List<UserDTO> dtos = userConvertor.toDTOList(pageInfo.getList());

        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        userGateway.save(entity);
    }

    public void update(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        userGateway.save(entity);
    }

    public void delete(Long id) {
        UserEntity user = userGateway.getById(id);
        if (user != null) {
            // 超级管理员保护：admin 用户不可删除
            if ("admin".equals(user.getUsername()) || "ADMIN".equals(user.getRole())) {
                throw new RuntimeException("超级管理员账号不可删除");
            }
            userGateway.deleteById(id);
        }
    }

    public UserDTO getById(Long id) {
        UserEntity entity = userGateway.getById(id);
        return userConvertor.toDTO(entity);
    }

    public void resetPassword(Long id) {
        // 实现密码重置逻辑
        UserEntity user = userGateway.getById(id);
        if (user != null) {
            // 这里可以添加密码重置逻辑，例如设置默认密码
            userGateway.save(user);
        }
    }

    public void changeStatus(Long id, Integer status) {
        // 实现状态切换逻辑
        UserEntity user = userGateway.getById(id);
        if (user != null) {
            // 超级管理员保护：admin 用户不可禁用
            if (0 == status && ("admin".equals(user.getUsername()) || "ADMIN".equals(user.getRole()))) {
                throw new RuntimeException("超级管理员账号不可禁用");
            }
            user.setStatus(status);
            userGateway.save(user);
        }
    }
}
