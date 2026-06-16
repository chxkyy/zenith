package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.UserService;
import com.zenith.admin.dto.system.cmd.UserAddCmd;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.UserPageQuery;
import com.zenith.admin.dto.system.cmd.UserUpdateCmd;
import com.zenith.admin.service.system.executor.cmd.UserChangeStatusCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserDeleteCmdExe;
import com.zenith.admin.service.system.executor.qry.UserGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.UserGetByLoginIdQryExe;
import com.zenith.admin.service.system.executor.qry.UserListByPageQryExe;
import com.zenith.admin.service.system.executor.cmd.UserResetPasswordCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserUpdateCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserListByPageQryExe userListByPageQryExe;
    private final UserSaveCmdExe userSaveCmdExe;
    private final UserUpdateCmdExe userUpdateCmdExe;
    private final UserDeleteCmdExe userDeleteCmdExe;
    private final UserGetByIdQryExe userGetByIdQryExe;
    private final UserGetByLoginIdQryExe userGetByLoginIdQryExe;
    private final UserResetPasswordCmdExe userResetPasswordCmdExe;
    private final UserChangeStatusCmdExe userChangeStatusCmdExe;

    @Override
    public PageInfo<UserDTO> listByPage(UserPageQuery query) {
        return userListByPageQryExe.execute(query);
    }

    @Override
    public void save(UserAddCmd cmd, Long currentUserId) {
        userSaveCmdExe.execute(cmd);
    }

    @Override
    public void update(UserUpdateCmd cmd, Long currentUserId) {
        userUpdateCmdExe.execute(cmd);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        userDeleteCmdExe.execute(id);
    }

    @Override
    public UserDTO getById(Long id) {
        return userGetByIdQryExe.execute(id);
    }

    @Override
    public UserDTO getByLoginId(String loginId) {
        return userGetByLoginIdQryExe.execute(loginId);
    }

    @Override
    public void resetPassword(Long id) {
        userResetPasswordCmdExe.execute(id);
    }

    @Override
    public void changeStatus(Long id, Integer status, Long currentUserId) {
        userChangeStatusCmdExe.execute(id, status);
    }
}
