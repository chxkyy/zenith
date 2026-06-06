package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.service.system.executor.converter.OperLogConvertor;
import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OperLogPageQryExe {

    private final OperLogMapper operLogMapper;
    private final OperLogConvertor operLogConvertor;

    public PageInfo<OperLogDTO> execute(int pageIndex, int pageSize, String operator, String module, String result) {
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

        PageInfo<OperLogDO> pageInfo = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPageInfo(() -> operLogMapper.selectList(queryWrapper));

        List<OperLogDTO> dtos = operLogConvertor.toDTOList(pageInfo.getList());

        PageInfo<OperLogDTO> pageResult = new PageInfo<>();
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setList(dtos);
        return pageResult;
    }
}
