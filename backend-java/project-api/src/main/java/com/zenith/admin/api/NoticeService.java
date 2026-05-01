package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;

import java.util.List;

public interface NoticeService {
    List<NoticeDTO> listAll();
    PageInfo<NoticeDTO> page(NoticePageQuery query);
    void save(NoticeDTO noticeDTO);
    void delete(Long id);
    NoticeDTO getById(Long id);
    void updateStatus(Long id, String status);
}
