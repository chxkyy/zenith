package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.model.NoticeEntity;
import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.infrastructure.convertor.NoticeConvertor;
import com.zenith.admin.infrastructure.dataobject.NoticeDO;
import com.zenith.admin.infrastructure.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeConvertor noticeConvertor;

    public MultiResponse<NoticeDTO> listAll() {
        List<NoticeDO> noticeDOS = noticeMapper.selectList(null);
        List<NoticeEntity> entities = noticeConvertor.toEntityList(noticeDOS);
        List<NoticeDTO> dtos = noticeConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(NoticeDTO noticeDTO) {
        NoticeEntity entity = noticeConvertor.toEntity(noticeDTO);
        NoticeDO noticeDO = noticeConvertor.toDataObject(entity);
        if (noticeDO.getId() == null) {
            noticeMapper.insert(noticeDO);
        } else {
            noticeMapper.updateById(noticeDO);
        }
    }

    public void update(NoticeDTO noticeDTO) {
        NoticeEntity entity = noticeConvertor.toEntity(noticeDTO);
        NoticeDO noticeDO = noticeConvertor.toDataObject(entity);
        noticeMapper.updateById(noticeDO);
    }

    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    public NoticeDTO getById(Long id) {
        NoticeDO noticeDO = noticeMapper.selectById(id);
        NoticeEntity entity = noticeConvertor.toEntity(noticeDO);
        return noticeConvertor.toDTO(entity);
    }
}
