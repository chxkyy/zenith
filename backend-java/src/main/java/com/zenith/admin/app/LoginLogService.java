package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.dto.LoginLogDTO;
import com.zenith.admin.infrastructure.convertor.LoginLogConvertor;
import com.zenith.admin.infrastructure.dataobject.LoginLogDO;
import com.zenith.admin.infrastructure.mapper.LoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private LoginLogConvertor loginLogConvertor;

    public PageResponse<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip) {
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
        
        List<LoginLogEntity> entities = loginLogConvertor.toEntityList(pageInfo.getList());
        List<LoginLogDTO> dtos = loginLogConvertor.toDTOList(entities);
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        loginLogMapper.deleteById(id);
    }

    public void save(LoginLogDTO loginLogDTO) {
        LoginLogEntity entity = loginLogConvertor.toEntity(loginLogDTO);
        loginLogMapper.insert(loginLogConvertor.toDataObject(entity));
    }
}
