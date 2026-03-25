package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.NoticeEntity;
import java.util.List;

public interface NoticeGateway {
    List<NoticeEntity> listAll();
    void save(NoticeEntity notice);
    NoticeEntity getById(Long id);
    void deleteById(Long id);
}
