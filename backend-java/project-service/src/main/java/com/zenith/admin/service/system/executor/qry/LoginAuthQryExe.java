package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.service.system.executor.converter.UserConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 登录认证查询执行器
 * 封装登录校验逻辑：查找用户 -> 校验密码 -> 检查状态 -> 返回用户信息
 */
@Component
@RequiredArgsConstructor
public class LoginAuthQryExe {

    private final UserMapper userMapper;
    private final UserConvertor userConvertor;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 执行登录认证，返回认证通过后的用户DTO
     *
     * @param loginId 登录账号
     * @param password 明文密码
     * @return 认证通过后的UserDTO
     * @throws BizException 用户不存在、密码错误或用户已禁用时抛出
     */
    public UserDTO execute(String loginId, String password) {
        UserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getLoginId, loginId)
        );

        if (user == null) {
            throw new BizException("USER_NOT_FOUND", "登录账号不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException("PASSWORD_ERROR", "密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BizException("USER_DISABLED", "用户已禁用");
        }

        return userConvertor.toDTO(user);
    }
}
