package com.zenith.admin;

import com.zenith.admin.dto.dataobject.FileDTO;
import com.zenith.admin.dataobject.FileDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileConvertor {
    FileConvertor INSTANCE = Mappers.getMapper(FileConvertor.class);

    FileDO toDataObject(FileDTO fileDTO);
    FileDTO toDTO(FileDO fileDO);
    List<FileDTO> toDTOList(List<FileDO> fileDOList);
    List<FileDO> toDataObjectList(List<FileDTO> fileDTOList);
}
