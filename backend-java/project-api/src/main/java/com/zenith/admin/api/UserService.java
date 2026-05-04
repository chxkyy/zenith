package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.UserAddCmd;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;
import com.zenith.admin.dto.data.UserUpdateCmd;

public interface UserService {
    void changeStatus(Long id, Integer status, Long currentUserId);

    void delete(Long id, Long currentUserId);

    UserDTO getById(Long id);

    PageInfo<UserDTO> listByPage(UserPageQuery query);

    void resetPassword(Long id);

    void save(UserAddCmd cmd, Long currentUserId);

    void update(UserUpdateCmd cmd, Long currentUserId);
}
