package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.LoginLogGateway;
import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.infrastructure.convertor.LoginLogConvertor;
import com.zenith.admin.infrastructure.dataobject.LoginLogDO;
import com.zenith.admin.infrastructure.mapper.LoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class LoginLogGatewayImpl implements LoginLogGateway {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private LoginLogConvertor loginLogConvertor;

    @Override
    public PageInfo<LoginLogEntity> listByPage(int pageIndex, int pageSize, String username, String status, String ip) {
        PageHelper.startPage(pageIndex, pageSize);
        LambdaQueryWrapper<LoginLogDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            queryWrapper.like(LoginLogDO::getUsername, username);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(LoginLogDO::getStatus, status);
        }
        if (StringUtils.hasText(ip)) {
            queryWrapper.like(LoginLogDO::getIp, ip);
        }
        queryWrapper.orderByDesc(LoginLogDO::getLoginAt);
        List<LoginLogDO> loginLogDOS = loginLogMapper.selectList(queryWrapper);
        PageInfo<LoginLogDO> pageInfo = new PageInfo<>(loginLogDOS);
        
        PageInfo<LoginLogEntity> resultPage = new PageInfo<>();
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setList(loginLogConvertor.toEntityList(pageInfo.getList()));
        return resultPage;
    }

    @Override
    public void save(LoginLogEntity log) {
        loginLogMapper.insert(loginLogConvertor.toDataObject(log));
    }

    @Override
    public void deleteById(Long id) {
        loginLogMapper.deleteById(id);
    }
}
