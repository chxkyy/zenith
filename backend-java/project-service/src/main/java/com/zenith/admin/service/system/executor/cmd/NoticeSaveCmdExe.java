package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.data.NoticeAddCmd;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeSaveCmdExe {

    private final NoticeMapper noticeMapper;

    public void execute(NoticeAddCmd cmd) {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setTitle(cmd.getTitle());
        noticeDO.setContent(cmd.getContent());
        noticeDO.setType(cmd.getType());
        noticeDO.setStatus(cmd.getStatus());
        noticeDO.setReadCount(0);
        noticeDO.setIsPinned(false);
        noticeMapper.insert(noticeDO);
    }
}
