package com.zenith.admin.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import com.zenith.admin.infrastructure.dataobject.UserDO;
import com.zenith.admin.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserConvertor userConvertor;

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(wrapper);

        if (userDO == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        if (userDO.getStatus() == 0) {
            result.put("success", false);
            result.put("message", "账号已被禁用");
            return result;
        }

        String token = UUID.randomUUID().toString().replace("-", "");

        result.put("success", true);
        result.put("token", token);
        result.put("username", userDO.getUsername());
        result.put("userId", userDO.getId());

        return result;
    }

    public Map<String, Object> logout(String token) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退出登录成功");
        return result;
    }

    public Map<String, Object> getCurrentUser(String token) {
        Map<String, Object> result = new HashMap<>();

        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        result.put("success", true);
        result.put("username", "admin");
        result.put("userId", 1L);

        return result;
    }

    public Map<String, Object> refreshToken(String token) {
        Map<String, Object> result = new HashMap<>();

        String newToken = UUID.randomUUID().toString().replace("-", "");

        result.put("success", true);
        result.put("token", newToken);

        return result;
    }
}
