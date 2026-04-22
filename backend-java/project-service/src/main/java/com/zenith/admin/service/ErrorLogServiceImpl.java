package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.ErrorLogService;
import com.zenith.admin.dto.data.ErrorLogDTO;
import com.zenith.admin.ErrorLogConvertor;
import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.mapper.ErrorLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogMapper errorLogMapper;
    private final ErrorLogConvertor errorLogConvertor;

    @Override
    public PageInfo<ErrorLogDTO> listByPage(int pageIndex, int pageSize, String module, String ip) {
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

        List<ErrorLogDTO> dtos = errorLogConvertor.toDTOList(pageInfo.getList());

        PageInfo<ErrorLogDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void delete(Long id) {
        errorLogMapper.deleteById(id);
    }

    @Override
    public void clear(int months) {
        LambdaQueryWrapper<ErrorLogDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(ErrorLogDO::getCreatedAt, LocalDateTime.now().minusMonths(months));
        errorLogMapper.delete(queryWrapper);
    }

    @Override
    public void save(ErrorLogDTO errorLogDTO) {
        ErrorLogDO errorLogDO = errorLogConvertor.toDataObject(errorLogDTO);
        errorLogMapper.insert(errorLogDO);
    }
}
