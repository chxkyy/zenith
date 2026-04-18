package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.dataobject.LoginLogDTO;
import com.zenith.admin.LoginLogConvertor;
import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogMapper loginLogMapper;
    private final LoginLogConvertor loginLogConvertor;

    public PageInfo<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip) {
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

        List<LoginLogDTO> dtos = loginLogConvertor.toDTOList(pageInfo.getList());

        PageInfo<LoginLogDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    public void delete(Long id) {
        loginLogMapper.deleteById(id);
    }

    public void save(LoginLogDTO loginLogDTO) {
        LoginLogDO loginLogDO = loginLogConvertor.toDataObject(loginLogDTO);
        loginLogMapper.insert(loginLogDO);
    }
}
