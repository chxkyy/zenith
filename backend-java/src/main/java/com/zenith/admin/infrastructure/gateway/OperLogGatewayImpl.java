package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.OperLogGateway;
import com.zenith.admin.domain.model.OperLogEntity;
import com.zenith.admin.infrastructure.convertor.OperLogConvertor;
import com.zenith.admin.infrastructure.dataobject.OperLogDO;
import com.zenith.admin.infrastructure.mapper.OperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class OperLogGatewayImpl implements OperLogGateway {

    @Autowired
    private OperLogMapper operLogMapper;

    @Autowired
    private OperLogConvertor operLogConvertor;

    @Override
    public PageInfo<OperLogEntity> listByPage(int pageIndex, int pageSize, String operator, String module, String result) {
        PageHelper.startPage(pageIndex, pageSize);
        LambdaQueryWrapper<OperLogDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(operator)) {
            queryWrapper.like(OperLogDO::getOperator, operator);
        }
        if (StringUtils.hasText(module)) {
            queryWrapper.eq(OperLogDO::getModule, module);
        }
        if (StringUtils.hasText(result)) {
            queryWrapper.eq(OperLogDO::getResult, result);
        }
        queryWrapper.orderByDesc(OperLogDO::getCreatedAt);
        List<OperLogDO> operLogDOS = operLogMapper.selectList(queryWrapper);
        PageInfo<OperLogDO> pageInfo = new PageInfo<>(operLogDOS);
        
        PageInfo<OperLogEntity> resultPage = new PageInfo<>();
        resultPage.setTotal(pageInfo.getTotal());
        resultPage.setList(operLogConvertor.toEntityList(pageInfo.getList()));
        return resultPage;
    }

    @Override
    public void save(OperLogEntity log) {
        operLogMapper.insert(operLogConvertor.toDataObject(log));
    }

    @Override
    public void deleteById(Long id) {
        operLogMapper.deleteById(id);
    }
}
