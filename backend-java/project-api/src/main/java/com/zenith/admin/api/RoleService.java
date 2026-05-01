package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;

import java.util.List;

public interface RoleService {
    List<RoleDTO> listAll();
    PageInfo<RoleDTO> listByPage(RolePageQuery query);
    void save(RoleDTO roleDTO);
    void update(RoleDTO roleDTO);
    void delete(Long id);
    RoleDTO getById(Long id);
    void changeStatus(Long id, Integer status);
}
