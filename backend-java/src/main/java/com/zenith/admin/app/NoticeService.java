package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.gateway.NoticeGateway;
import com.zenith.admin.domain.model.NoticeEntity;
import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.infrastructure.convertor.NoticeConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeGateway noticeGateway;

    @Autowired
    private NoticeConvertor noticeConvertor;

    public MultiResponse<NoticeDTO> listAll() {
        List<NoticeEntity> entities = noticeGateway.listAll();
        List<NoticeDTO> dtos = noticeConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(NoticeDTO noticeDTO) {
        NoticeEntity entity = noticeConvertor.toEntity(noticeDTO);
        noticeGateway.save(entity);
    }

    public void update(NoticeDTO noticeDTO) {
        NoticeEntity entity = noticeConvertor.toEntity(noticeDTO);
        noticeGateway.save(entity);
    }

    public void delete(Long id) {
        noticeGateway.deleteById(id);
    }

    public NoticeDTO getById(Long id) {
        NoticeEntity entity = noticeGateway.getById(id);
        return noticeConvertor.toDTO(entity);
    }
}
