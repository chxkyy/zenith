package com.zenith.admin;

import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.dto.dataobject.NoticeDTO;
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
public class NoticeConvertorImpl implements NoticeConvertor {

    @Override
    public NoticeDO toDataObject(NoticeDTO noticeDTO) {
        if ( noticeDTO == null ) {
            return null;
        }

        NoticeDO noticeDO = new NoticeDO();

        noticeDO.setId( noticeDTO.getId() );
        noticeDO.setTitle( noticeDTO.getTitle() );
        noticeDO.setType( noticeDTO.getType() );
        noticeDO.setAuthor( noticeDTO.getAuthor() );
        noticeDO.setContent( noticeDTO.getContent() );
        noticeDO.setStatus( noticeDTO.getStatus() );
        noticeDO.setRemark( noticeDTO.getRemark() );
        noticeDO.setIsPinned( noticeDTO.getIsPinned() );
        noticeDO.setReadCount( noticeDTO.getReadCount() );
        noticeDO.setCreatedAt( noticeDTO.getCreatedAt() );
        noticeDO.setUpdatedAt( noticeDTO.getUpdatedAt() );

        return noticeDO;
    }

    @Override
    public NoticeDTO toDTO(NoticeDO noticeDO) {
        if ( noticeDO == null ) {
            return null;
        }

        NoticeDTO noticeDTO = new NoticeDTO();

        noticeDTO.setId( noticeDO.getId() );
        noticeDTO.setTitle( noticeDO.getTitle() );
        noticeDTO.setType( noticeDO.getType() );
        noticeDTO.setAuthor( noticeDO.getAuthor() );
        noticeDTO.setContent( noticeDO.getContent() );
        noticeDTO.setStatus( noticeDO.getStatus() );
        noticeDTO.setRemark( noticeDO.getRemark() );
        noticeDTO.setIsPinned( noticeDO.getIsPinned() );
        noticeDTO.setReadCount( noticeDO.getReadCount() );
        noticeDTO.setCreatedAt( noticeDO.getCreatedAt() );
        noticeDTO.setUpdatedAt( noticeDO.getUpdatedAt() );

        return noticeDTO;
    }

    @Override
    public List<NoticeDTO> toDTOList(List<NoticeDO> noticeDOList) {
        if ( noticeDOList == null ) {
            return null;
        }

        List<NoticeDTO> list = new ArrayList<NoticeDTO>( noticeDOList.size() );
        for ( NoticeDO noticeDO : noticeDOList ) {
            list.add( toDTO( noticeDO ) );
        }

        return list;
    }

    @Override
    public List<NoticeDO> toDataObjectList(List<NoticeDTO> noticeDTOList) {
        if ( noticeDTOList == null ) {
            return null;
        }

        List<NoticeDO> list = new ArrayList<NoticeDO>( noticeDTOList.size() );
        for ( NoticeDTO noticeDTO : noticeDTOList ) {
            list.add( toDataObject( noticeDTO ) );
        }

        return list;
    }
}
