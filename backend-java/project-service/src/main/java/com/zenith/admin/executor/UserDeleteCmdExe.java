package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeleteCmdExe {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public void execute(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if ("admin".equals(userDO.getLoginId())) {
                throw new BizException("USER_DELETE_001", "超级管理员账号不可删除");
            }
            
            LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleDO::getUserId, id);
            userRoleMapper.delete(wrapper);
            
            userMapper.deleteById(id);
        }
    }
}
