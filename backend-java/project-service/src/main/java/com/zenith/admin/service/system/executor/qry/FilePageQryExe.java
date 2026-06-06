package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.query.FilePageQuery;
import com.zenith.admin.mapper.FileMapper;
import com.zenith.admin.service.system.executor.converter.FileConvertor;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilePageQryExe {
    private final FileConvertor fileConvertor;
    private final FileMapper fileMapper;

    public PageInfo<FileDTO> execute(FilePageQuery query) {
        LambdaQueryWrapper<FileDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(FileDO::getName, query.getKeyword());
        }

        queryWrapper.orderByDesc(FileDO::getCreatedTime);

        PageInfo<FileDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> fileMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, fileConvertor::toDTOList);
    }
}
