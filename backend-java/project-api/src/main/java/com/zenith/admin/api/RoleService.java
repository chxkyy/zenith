package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.RoleAddCmd;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;
import com.zenith.admin.dto.data.RoleUpdateCmd;

import java.util.List;

public interface RoleService {
    List<RoleDTO> listAll();
    List<RoleDTO> listActiveRoles();
    PageInfo<RoleDTO> listByPage(RolePageQuery query);
    void save(RoleAddCmd cmd, Long currentUserId);
    void update(RoleUpdateCmd cmd, Long currentUserId);
    void delete(Long id, Long currentUserId);
    RoleDTO getById(Long id);
    void changeStatus(Long id, Integer status, Long currentUserId);
}
