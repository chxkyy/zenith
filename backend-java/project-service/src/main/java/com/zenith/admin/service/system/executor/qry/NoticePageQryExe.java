package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.data.NoticeDTO;
import com.zenith.admin.dto.system.qry.NoticePageQuery;
import com.zenith.admin.service.system.executor.converter.NoticeConvertor;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticePageQryExe {

    private final NoticeMapper noticeMapper;
    private final NoticeConvertor noticeConvertor;

    public PageInfo<NoticeDTO> execute(NoticePageQuery query) {
        LambdaQueryWrapper<NoticeDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(NoticeDO::getTitle, query.getKeyword());
        }

        if (query.getType() != null && !query.getType().isEmpty()) {
            queryWrapper.eq(NoticeDO::getType, query.getType());
        }

        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            queryWrapper.eq(NoticeDO::getStatus, query.getStatus());
        }

        queryWrapper.orderByDesc(NoticeDO::getCreatedTime);

        PageInfo<NoticeDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> noticeMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, noticeConvertor::toDTOList);
    }
}
