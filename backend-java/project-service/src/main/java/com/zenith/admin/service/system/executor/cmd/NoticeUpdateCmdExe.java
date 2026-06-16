package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.cmd.NoticeUpdateCmd;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeUpdateCmdExe {

    private final NoticeMapper noticeMapper;

    public void execute(NoticeUpdateCmd cmd) {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setId(cmd.getId());
        noticeDO.setTitle(cmd.getTitle());
        noticeDO.setContent(cmd.getContent());
        noticeDO.setType(cmd.getType());
        noticeDO.setStatus(cmd.getStatus());
        noticeMapper.updateById(noticeDO);
    }
}
