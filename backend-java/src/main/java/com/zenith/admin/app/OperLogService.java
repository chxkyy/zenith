package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.OperLogEntity;
import com.zenith.admin.dto.OperLogDTO;
import com.zenith.admin.infrastructure.convertor.OperLogConvertor;
import com.zenith.admin.infrastructure.dataobject.OperLogDO;
import com.zenith.admin.infrastructure.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperLogService {

    private final OperLogMapper operLogMapper;
    private final OperLogConvertor operLogConvertor;

    public PageResponse<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result) {
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

        List<OperLogEntity> entities = operLogConvertor.toEntityList(pageInfo.getList());
        List<OperLogDTO> dtos = operLogConvertor.toDTOList(entities);
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        operLogMapper.deleteById(id);
    }

    public void save(OperLogDTO operLogDTO) {
        OperLogEntity entity = operLogConvertor.toEntity(operLogDTO);
        operLogMapper.insert(operLogConvertor.toDataObject(entity));
    }
}
