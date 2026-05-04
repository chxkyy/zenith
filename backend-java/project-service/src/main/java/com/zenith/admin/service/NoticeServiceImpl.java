package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.NoticeService;
import com.zenith.admin.dto.data.NoticeAddCmd;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.dto.data.NoticeUpdateCmd;
import com.zenith.admin.NoticeConvertor;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final NoticeConvertor noticeConvertor;

    @Override
    public List<NoticeDTO> listAll() {
        List<NoticeDO> noticeDOS = noticeMapper.selectList(null);
        List<NoticeDTO> noticeDTOS = noticeConvertor.toDTOList(noticeDOS);
        return noticeDTOS;
    }

    @Override
    public PageInfo<NoticeDTO> page(NoticePageQuery query) {
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

        queryWrapper.orderByDesc(NoticeDO::getCreatedTime);

        List<NoticeDO> noticeDOS = noticeMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<NoticeDO> pageInfo = new com.github.pagehelper.PageInfo<>(noticeDOS);
        List<NoticeDTO> noticeDTOS = noticeConvertor.toDTOList(pageInfo.getList());

        PageInfo<NoticeDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(noticeDTOS);
        return result;
    }

    @Override
    public void save(NoticeAddCmd cmd, Long currentUserId) {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setTitle(cmd.getTitle());
        noticeDO.setContent(cmd.getContent());
        noticeDO.setType(cmd.getType());
        noticeDO.setStatus(cmd.getStatus());
        noticeDO.setReadCount(0);
        noticeDO.setIsPinned(false);
        noticeMapper.insert(noticeDO);
    }

    @Override
    public void update(NoticeUpdateCmd cmd, Long currentUserId) {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setId(cmd.getId());
        noticeDO.setTitle(cmd.getTitle());
        noticeDO.setContent(cmd.getContent());
        noticeDO.setType(cmd.getType());
        noticeDO.setStatus(cmd.getStatus());
        noticeMapper.updateById(noticeDO);
    }

    @Override
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public NoticeDTO getById(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        return noticeConvertor.toDTO(noticeDO);
    }

    @Override
    public void updateStatus(Long id, String status, Long currentUserId) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        if (noticeDO != null) {
            noticeDO.setStatus(status);
            noticeMapper.updateById(noticeDO);
        }
    }
}
