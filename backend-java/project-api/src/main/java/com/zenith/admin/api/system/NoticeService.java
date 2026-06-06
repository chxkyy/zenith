package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.NoticeAddCmd;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.dto.data.NoticeUpdateCmd;

import java.util.List;

public interface NoticeService {
    List<NoticeDTO> listAll();
    PageInfo<NoticeDTO> page(NoticePageQuery query);
    void save(NoticeAddCmd cmd, Long currentUserId);
    void update(NoticeUpdateCmd cmd, Long currentUserId);
    void delete(Long id);
    NoticeDTO getById(Long id);
    void updateStatus(Long id, String status, Long currentUserId);
}
