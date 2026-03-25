package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.UserEntity;
import com.github.pagehelper.PageInfo;

public interface UserGateway {
    PageInfo<UserEntity> listByPage(int pageNum, int pageSize);
    void save(UserEntity user);
    UserEntity getById(Long id);
    void deleteById(Long id);
}
