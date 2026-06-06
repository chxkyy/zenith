package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.NoticeService;
import com.zenith.admin.dto.data.NoticeAddCmd;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.dto.data.NoticeUpdateCmd;
import com.zenith.admin.service.system.executor.cmd.NoticeDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.NoticeSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.NoticeUpdateCmdExe;
import com.zenith.admin.service.system.executor.cmd.NoticeUpdateStatusCmdExe;
import com.zenith.admin.service.system.executor.qry.NoticeGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.NoticeListAllQryExe;
import com.zenith.admin.service.system.executor.qry.NoticePageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeListAllQryExe noticeListAllQryExe;
    private final NoticePageQryExe noticePageQryExe;
    private final NoticeGetByIdQryExe noticeGetByIdQryExe;
    private final NoticeSaveCmdExe noticeSaveCmdExe;
    private final NoticeUpdateCmdExe noticeUpdateCmdExe;
    private final NoticeDeleteCmdExe noticeDeleteCmdExe;
    private final NoticeUpdateStatusCmdExe noticeUpdateStatusCmdExe;

    @Override
    public List<NoticeDTO> listAll() {
        return noticeListAllQryExe.execute();
    }

    @Override
    public PageInfo<NoticeDTO> page(NoticePageQuery query) {
        return noticePageQryExe.execute(query);
    }

    @Override
    public void save(NoticeAddCmd cmd, Long currentUserId) {
        noticeSaveCmdExe.execute(cmd);
    }

    @Override
    public void update(NoticeUpdateCmd cmd, Long currentUserId) {
        noticeUpdateCmdExe.execute(cmd);
    }

    @Override
    public void delete(Long id) {
        noticeDeleteCmdExe.execute(id);
    }

    @Override
    public NoticeDTO getById(Long id) {
        return noticeGetByIdQryExe.execute(id);
    }

    @Override
    public void updateStatus(Long id, String status, Long currentUserId) {
        noticeUpdateStatusCmdExe.execute(id, status);
    }
}
