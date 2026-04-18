package com.zenith.admin;

import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dto.dataobject.FileDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T16:08:19+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class FileConvertorImpl implements FileConvertor {

    @Override
    public FileDO toDataObject(FileDTO fileDTO) {
        if ( fileDTO == null ) {
            return null;
        }

        FileDO fileDO = new FileDO();

        fileDO.setId( fileDTO.getId() );
        fileDO.setName( fileDTO.getName() );
        fileDO.setOriginalName( fileDTO.getOriginalName() );
        fileDO.setPath( fileDTO.getPath() );
        fileDO.setType( fileDTO.getType() );
        fileDO.setSize( fileDTO.getSize() );
        fileDO.setUploader( fileDTO.getUploader() );
        fileDO.setCreatedAt( fileDTO.getCreatedAt() );

        return fileDO;
    }

    @Override
    public FileDTO toDTO(FileDO fileDO) {
        if ( fileDO == null ) {
            return null;
        }

        FileDTO fileDTO = new FileDTO();

        fileDTO.setId( fileDO.getId() );
        fileDTO.setName( fileDO.getName() );
        fileDTO.setOriginalName( fileDO.getOriginalName() );
        fileDTO.setPath( fileDO.getPath() );
        fileDTO.setType( fileDO.getType() );
        fileDTO.setSize( fileDO.getSize() );
        fileDTO.setUploader( fileDO.getUploader() );
        fileDTO.setCreatedAt( fileDO.getCreatedAt() );

        return fileDTO;
    }

    @Override
    public List<FileDTO> toDTOList(List<FileDO> fileDOList) {
        if ( fileDOList == null ) {
            return null;
        }

        List<FileDTO> list = new ArrayList<FileDTO>( fileDOList.size() );
        for ( FileDO fileDO : fileDOList ) {
            list.add( toDTO( fileDO ) );
        }

        return list;
    }

    @Override
    public List<FileDO> toDataObjectList(List<FileDTO> fileDTOList) {
        if ( fileDTOList == null ) {
            return null;
        }

        List<FileDO> list = new ArrayList<FileDO>( fileDTOList.size() );
        for ( FileDTO fileDTO : fileDTOList ) {
            list.add( toDataObject( fileDTO ) );
        }

        return list;
    }
}
