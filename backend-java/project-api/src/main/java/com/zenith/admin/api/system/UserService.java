package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.cmd.UserAddCmd;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.UserPageQuery;
import com.zenith.admin.dto.system.cmd.UserUpdateCmd;

public interface UserService {
    void changeStatus(Long id, Integer status, Long currentUserId);

    void delete(Long id, Long currentUserId);

    UserDTO getById(Long id);

    UserDTO getByLoginId(String loginId);

    PageInfo<UserDTO> listByPage(UserPageQuery query);

    void resetPassword(Long id);

    void save(UserAddCmd cmd, Long currentUserId);

    void update(UserUpdateCmd cmd, Long currentUserId);
}
