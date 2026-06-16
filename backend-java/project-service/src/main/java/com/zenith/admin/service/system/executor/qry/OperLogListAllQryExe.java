package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dto.system.data.OperLogDTO;
import com.zenith.admin.service.system.executor.converter.OperLogConvertor;
import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OperLogListAllQryExe {

    private final OperLogMapper operLogMapper;
    private final OperLogConvertor operLogConvertor;

    public List<OperLogDTO> execute(String operator, String module, String result) {
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
        queryWrapper.orderByDesc(OperLogDO::getCreatedTime);
        List<OperLogDO> operLogDOS = operLogMapper.selectList(queryWrapper);
        return operLogConvertor.toDTOList(operLogDOS);
    }
}
