package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.NoticeEntity;
import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.infrastructure.dataobject.NoticeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoticeConvertor {
    NoticeConvertor INSTANCE = Mappers.getMapper(NoticeConvertor.class);

    NoticeEntity toEntity(NoticeDO noticeDO);

    NoticeEntity toEntity(NoticeDTO noticeDTO);

    NoticeDO toDataObject(NoticeEntity noticeEntity);

    NoticeDTO toDTO(NoticeEntity noticeEntity);

    List<NoticeEntity> toEntityList(List<NoticeDO> noticeDOList);

    List<NoticeDTO> toDTOList(List<NoticeEntity> noticeEntityList);
}
