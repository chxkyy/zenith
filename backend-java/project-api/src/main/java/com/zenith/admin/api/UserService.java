package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;

public interface UserService {
    void changeStatus(Long id, Integer status);

    void delete(Long id);

    UserDTO getById(Long id);

    PageInfo<UserDTO> listByPage(UserPageQuery query);

    void resetPassword(Long id);

    void save(UserDTO userDTO);

    void update(UserDTO userDTO);
}
