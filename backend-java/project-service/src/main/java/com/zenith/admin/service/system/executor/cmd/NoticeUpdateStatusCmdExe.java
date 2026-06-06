package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeUpdateStatusCmdExe {

    private final NoticeMapper noticeMapper;

    public void execute(Long id, String status) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        if (noticeDO != null) {
            noticeDO.setStatus(status);
            noticeMapper.updateById(noticeDO);
        }
    }
}
