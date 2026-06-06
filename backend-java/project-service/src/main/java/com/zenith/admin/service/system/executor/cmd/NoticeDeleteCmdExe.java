package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeDeleteCmdExe {

    private final NoticeMapper noticeMapper;

    public void execute(Long id) {
        noticeMapper.deleteById(id);
    }
}
