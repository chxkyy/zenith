package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dto.system.data.NoticeDTO;
import com.zenith.admin.service.system.executor.converter.NoticeConvertor;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeGetByIdQryExe {

    private final NoticeMapper noticeMapper;
    private final NoticeConvertor noticeConvertor;

    public NoticeDTO execute(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        return noticeConvertor.toDTO(noticeDO);
    }
}
