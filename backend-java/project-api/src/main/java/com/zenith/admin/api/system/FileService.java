package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.query.FilePageQuery;

import java.io.IOException;

public interface FileService {
    PageInfo<FileDTO> page(FilePageQuery query);
    FileDTO upload(byte[] fileContent, String originalFilename, String contentType, long fileSize, Long currentUserId) throws IOException;
    void delete(Long id, Long currentUserId);
    FileDTO getById(Long id);
}
