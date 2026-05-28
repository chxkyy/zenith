package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.RoleService;
import com.zenith.admin.dto.data.RoleAddCmd;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;
import com.zenith.admin.dto.data.RoleUpdateCmd;
import com.zenith.admin.executor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleChangeStatusCmdExe roleChangeStatusCmdExe;
    private final RoleDeleteCmdExe roleDeleteCmdExe;
    private final RoleGetByIdQryExe roleGetByIdQryExe;
    private final RoleListActiveRolesQryExe roleListActiveRolesQryExe;
    private final RoleListAllQryExe roleListAllQryExe;
    private final RoleListByPageQryExe roleListByPageQryExe;
    private final RoleSaveCmdExe roleSaveCmdExe;
    private final RoleUpdateCmdExe roleUpdateCmdExe;

    @Override
    public void changeStatus(Long id, Integer status, Long currentUserId) {
        roleChangeStatusCmdExe.execute(id, status, currentUserId);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        roleDeleteCmdExe.execute(id);
    }

    @Override
    public RoleDTO getById(Long id) {
        return roleGetByIdQryExe.execute(id);
    }

    @Override
    public List<RoleDTO> listActiveRoles() {
        return roleListActiveRolesQryExe.execute();
    }

    @Override
    public List<RoleDTO> listAll() {
        return roleListAllQryExe.execute();
    }

    @Override
    public PageInfo<RoleDTO> listByPage(RolePageQuery query) {
        return roleListByPageQryExe.execute(query);
    }

    @Override
    public void save(RoleAddCmd cmd, Long currentUserId) {
        roleSaveCmdExe.execute(cmd, currentUserId);
    }

    @Override
    public void update(RoleUpdateCmd cmd, Long currentUserId) {
        roleUpdateCmdExe.execute(cmd, currentUserId);
    }
}
