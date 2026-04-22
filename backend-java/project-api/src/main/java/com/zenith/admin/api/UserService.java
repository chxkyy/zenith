package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;

public interface UserService {
    PageInfo<UserDTO> listByPage(UserPageQuery query);
    void save(UserDTO userDTO);
    void update(UserDTO userDTO);
    void delete(Long id);
    UserDTO getById(Long id);
    void resetPassword(Long id);
    void changeStatus(Long id, Integer status);
}
