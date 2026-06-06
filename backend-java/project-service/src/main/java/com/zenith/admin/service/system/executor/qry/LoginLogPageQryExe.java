package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.service.system.executor.converter.LoginLogConvertor;
import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginLogPageQryExe {

    private final LoginLogMapper loginLogMapper;
    private final LoginLogConvertor loginLogConvertor;

    public PageInfo<LoginLogDTO> execute(int pageIndex, int pageSize, String username, String status, String ip) {
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

        PageInfo<LoginLogDO> pageInfo = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPageInfo(() -> loginLogMapper.selectList(queryWrapper));

        List<LoginLogDTO> dtos = loginLogConvertor.toDTOList(pageInfo.getList());

        PageInfo<LoginLogDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }
}
