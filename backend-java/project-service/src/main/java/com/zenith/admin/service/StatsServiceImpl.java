package com.zenith.admin.service;

import com.zenith.admin.api.StatsService;
import com.zenith.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final OrgMapper orgMapper;
    private final MenuMapper menuMapper;
    private final OperLogMapper operLogMapper;
    private final LoginLogMapper loginLogMapper;
    private final ErrorLogMapper errorLogMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();

        data.put("totalUsers", userMapper.selectCount(null));
        data.put("totalRoles", roleMapper.selectCount(null));
        data.put("totalOrgs", orgMapper.selectCount(null));
        data.put("totalMenus", menuMapper.selectCount(null));

        data.put("activeUsers", userMapper.selectCount(null));
        data.put("onlineUsers", 0);

        return data;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", userMapper.selectCount(null));
        data.put("active", userMapper.selectCount(null));
        return data;
    }

    @Override
    public Map<String, Object> getRoleStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", roleMapper.selectCount(null));
        return data;
    }

    @Override
    public Map<String, Object> getOrgStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", orgMapper.selectCount(null));
        return data;
    }

    @Override
    public Map<String, Object> getMenuStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", menuMapper.selectCount(null));
        return data;
    }

    @Override
    public Map<String, Object> getLogStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("operLogs", operLogMapper.selectCount(null));
        data.put("loginLogs", loginLogMapper.selectCount(null));
        data.put("errorLogs", errorLogMapper.selectCount(null));
        return data;
    }
}
