package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dto.system.data.NoticeDTO;
import com.zenith.admin.service.system.executor.converter.NoticeConvertor;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoticeListAllQryExe {

    private final NoticeMapper noticeMapper;
    private final NoticeConvertor noticeConvertor;

    public List<NoticeDTO> execute() {
        List<NoticeDO> noticeDOS = noticeMapper.selectList(null);
        return noticeConvertor.toDTOList(noticeDOS);
    }
}
