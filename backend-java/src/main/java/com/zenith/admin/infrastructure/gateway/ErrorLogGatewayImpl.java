package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.ErrorLogGateway;
import com.zenith.admin.domain.model.ErrorLogEntity;
import com.zenith.admin.infrastructure.convertor.ErrorLogConvertor;
import com.zenith.admin.infrastructure.dataobject.ErrorLogDO;
import com.zenith.admin.infrastructure.mapper.ErrorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ErrorLogGatewayImpl implements ErrorLogGateway {

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Autowired
    private ErrorLogConvertor errorLogConvertor;

    @Override
    public PageInfo<ErrorLogEntity> listByPage(int pageIndex, int pageSize, String module, String ip) {
        PageHelper.startPage(pageIndex, pageSize);
        LambdaQueryWrapper<ErrorLogDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            queryWrapper.eq(ErrorLogDO::getModule, module);
        }
        if (StringUtils.hasText(ip)) {
            queryWrapper.like(ErrorLogDO::getIp, ip);
        }
        queryWrapper.orderByDesc(ErrorLogDO::getCreatedAt);
        List<ErrorLogDO> errorLogDOS = errorLogMapper.selectList(queryWrapper);
        PageInfo<ErrorLogDO> pageInfo = new PageInfo<>(errorLogDOS);
        
        PageInfo<ErrorLogEntity> resultPage = new PageInfo<>();
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setList(errorLogConvertor.toEntityList(pageInfo.getList()));
        return resultPage;
    }

    @Override
    public void save(ErrorLogEntity log) {
        errorLogMapper.insert(errorLogConvertor.toDataObject(log));
    }

    @Override
    public void deleteById(Long id) {
        errorLogMapper.deleteById(id);
    }

    @Override
    public void clearLogs(int months) {
        LambdaQueryWrapper<ErrorLogDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(ErrorLogDO::getCreatedAt, LocalDateTime.now().minusMonths(months));
        errorLogMapper.delete(queryWrapper);
    }
}
