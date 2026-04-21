package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.OperLogConvertor;
import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperLogService {

    private final OperLogMapper operLogMapper;
    private final OperLogConvertor operLogConvertor;

    public PageInfo<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result) {
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

        List<OperLogDTO> dtos = operLogConvertor.toDTOList(pageInfo.getList());

        PageInfo<OperLogDTO> pageResult = new PageInfo<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setList(dtos);
        return pageResult;
    }

    public void delete(Long id) {
        operLogMapper.deleteById(id);
    }

    public void save(OperLogDTO operLogDTO) {
        OperLogDO operLogDO = operLogConvertor.toDataObject(operLogDTO);
        operLogMapper.insert(operLogDO);
    }
}
