package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zenith.admin.api.TokenService;
import com.zenith.admin.dataobject.OnlineUserDO;
import com.zenith.admin.dto.data.OnlineUserDTO;
import com.zenith.admin.mapper.OnlineUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl extends ServiceImpl<OnlineUserMapper, OnlineUserDO> implements TokenService {

    @Override
    public void deleteToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        remove(new LambdaQueryWrapper<OnlineUserDO>()
                .eq(OnlineUserDO::getToken, token));
    }

    @Override
    public void deleteTokenByUserId(Long userId) {
        if (userId == null) {
            return;
        }

        remove(new LambdaQueryWrapper<OnlineUserDO>()
                .eq(OnlineUserDO::getUserId, userId));
    }

    @Override
    public String generateToken(Long userId, String ip) {
        String token = UUID.randomUUID().toString().replace("-", "");

        OnlineUserDO existingUser = getOne(new LambdaQueryWrapper<OnlineUserDO>()
                .eq(OnlineUserDO::getUserId, userId));

        if (existingUser != null) {
            existingUser.setToken(token);
            existingUser.setLoginTime(LocalDateTime.now());
            existingUser.setLastAccessTime(LocalDateTime.now());
            existingUser.setIp(ip);
            updateById(existingUser);
        } else {
            OnlineUserDO onlineUser = new OnlineUserDO();
            onlineUser.setUserId(userId);
            onlineUser.setToken(token);
            onlineUser.setLoginTime(LocalDateTime.now());
            onlineUser.setLastAccessTime(LocalDateTime.now());
            onlineUser.setIp(ip);
            save(onlineUser);
        }

        return token;
    }

    @Override
    public void refreshToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        OnlineUserDO onlineUser = getOne(new LambdaQueryWrapper<OnlineUserDO>()
                .eq(OnlineUserDO::getToken, token));

        if (onlineUser != null) {
            onlineUser.setLastAccessTime(LocalDateTime.now());
            updateById(onlineUser);
        }
    }

    @Override
    public OnlineUserDTO validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        OnlineUserDO onlineUser = getOne(new LambdaQueryWrapper<OnlineUserDO>()
                .eq(OnlineUserDO::getToken, token));

        if (onlineUser != null) {
            refreshToken(token);
            return convertToDTO(onlineUser);
        }

        return null;
    }

    private OnlineUserDTO convertToDTO(OnlineUserDO onlineUserDO) {
        OnlineUserDTO dto = new OnlineUserDTO();
        dto.setId(onlineUserDO.getId());
        dto.setUserId(onlineUserDO.getUserId());
        dto.setToken(onlineUserDO.getToken());
        dto.setIp(onlineUserDO.getIp());
        if (onlineUserDO.getLoginTime() != null) {
            dto.setLoginTime(onlineUserDO.getLoginTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        if (onlineUserDO.getLastAccessTime() != null) {
            dto.setLastAccessTime(onlineUserDO.getLastAccessTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        return dto;
    }
}