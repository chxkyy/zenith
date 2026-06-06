package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.ErrorLogDTO;
import com.zenith.admin.service.system.executor.converter.ErrorLogConvertor;
import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.mapper.ErrorLogMapper;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ErrorLogPageQryExe {

    private final ErrorLogMapper errorLogMapper;
    private final ErrorLogConvertor errorLogConvertor;

    public PageInfo<ErrorLogDTO> execute(int pageIndex, int pageSize, String module, String ip) {
        LambdaQueryWrapper<ErrorLogDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            queryWrapper.eq(ErrorLogDO::getModule, module);
        }
        if (StringUtils.hasText(ip)) {
            queryWrapper.like(ErrorLogDO::getIp, ip);
        }
        queryWrapper.orderByDesc(ErrorLogDO::getCreatedTime);

        PageInfo<ErrorLogDO> pageInfo = PageHelper.startPage(pageIndex, pageSize)
                .doSelectPageInfo(() -> errorLogMapper.selectList(queryWrapper));

        return PageResponseUtils.convert(pageInfo, errorLogConvertor::toDTOList);
    }
}
