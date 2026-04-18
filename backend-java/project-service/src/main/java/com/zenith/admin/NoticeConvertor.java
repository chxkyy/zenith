package com.zenith.admin;

import com.zenith.admin.dto.dataobject.NoticeDTO;
import com.zenith.admin.dataobject.NoticeDO;
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
