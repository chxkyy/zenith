package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.dto.NoticeDTO;
import com.zenith.admin.infrastructure.dataobject.NoticeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoticeConvertor {
    NoticeConvertor INSTANCE = Mappers.getMapper(NoticeConvertor.class);

    NoticeDO toDataObject(NoticeDTO noticeDTO);

    NoticeDTO toDTO(NoticeDO noticeDO);

    List<NoticeDTO> toDTOList(List<NoticeDO> noticeDOList);

    List<NoticeDO> toDataObjectList(List<NoticeDTO> noticeDTOList);
}
