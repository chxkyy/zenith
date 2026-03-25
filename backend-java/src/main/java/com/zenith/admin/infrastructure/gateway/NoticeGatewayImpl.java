package com.zenith.admin.infrastructure.gateway;

import com.zenith.admin.domain.gateway.NoticeGateway;
import com.zenith.admin.domain.model.NoticeEntity;
import com.zenith.admin.infrastructure.convertor.NoticeConvertor;
import com.zenith.admin.infrastructure.dataobject.NoticeDO;
import com.zenith.admin.infrastructure.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoticeGatewayImpl implements NoticeGateway {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeConvertor noticeConvertor;

    @Override
    public List<NoticeEntity> listAll() {
        List<NoticeDO> noticeDOS = noticeMapper.selectList(null);
        return noticeConvertor.toEntityList(noticeDOS);
    }

    @Override
    public void save(NoticeEntity notice) {
        NoticeDO noticeDO = noticeConvertor.toDataObject(notice);
        if (noticeDO.getId() == null) {
            noticeMapper.insert(noticeDO);
        } else {
            noticeMapper.updateById(noticeDO);
        }
    }

    @Override
    public NoticeEntity getById(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        return noticeConvertor.toEntity(noticeDO);
    }

    @Override
    public void deleteById(Long id) {
        noticeMapper.deleteById(id);
    }
}
