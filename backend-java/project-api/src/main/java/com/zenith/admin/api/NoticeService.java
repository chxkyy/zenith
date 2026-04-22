package com.zenith.admin.api;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;

public interface NoticeService {
    MultiResponse<NoticeDTO> listAll();
    PageInfo<NoticeDTO> page(NoticePageQuery query);
    void save(NoticeDTO noticeDTO);
    void delete(Long id);
    NoticeDTO getById(Long id);
    void updateStatus(Long id, String status);
}
