package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.dto.NoticePageQuery;
import com.zenith.admin.infrastructure.convertor.NoticeConvertor;
import com.zenith.admin.infrastructure.dataobject.NoticeDO;
import com.zenith.admin.infrastructure.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeConvertor noticeConvertor;

    public MultiResponse<NoticeDTO> listAll() {
        List<NoticeDO> noticeDOS = noticeMapper.selectList(null);
        List<NoticeDTO> noticeDTOS = noticeConvertor.toDTOList(noticeDOS);
        return MultiResponse.of(noticeDTOS);
    }

    public PageResponse<NoticeDTO> page(NoticePageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
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

        queryWrapper.orderByDesc(NoticeDO::getCreatedAt);

        List<NoticeDO> noticeDOS = noticeMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<NoticeDO> pageInfo = new com.github.pagehelper.PageInfo<>(noticeDOS);
        List<NoticeDTO> noticeDTOS = noticeConvertor.toDTOList(pageInfo.getList());

        return PageResponse.of(noticeDTOS, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(NoticeDTO noticeDTO) {
        NoticeDO noticeDO = noticeConvertor.toDataObject(noticeDTO);

        if (noticeDO.getId() == null) {
            noticeDO.setReadCount(0);
            noticeDO.setIsPinned(false);
            noticeDO.setCreatedAt(LocalDateTime.now());
            noticeDO.setUpdatedAt(LocalDateTime.now());
            noticeMapper.insert(noticeDO);
        } else {
            noticeDO.setUpdatedAt(LocalDateTime.now());
            noticeMapper.updateById(noticeDO);
        }
    }

    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    public NoticeDTO getById(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        return noticeConvertor.toDTO(noticeDO);
    }

    public void updateStatus(Long id, String status) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        if (noticeDO != null) {
            noticeDO.setStatus(status);
            noticeDO.setUpdatedAt(LocalDateTime.now());
            noticeMapper.updateById(noticeDO);
        }
    }
}
