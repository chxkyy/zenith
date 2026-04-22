package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.data.FilePageQuery;

import java.io.IOException;

public interface FileService {
    PageInfo<FileDTO> page(FilePageQuery query);
    FileDTO upload(byte[] fileContent, String originalFilename, String contentType, long fileSize) throws IOException;
    void delete(Long id);
    FileDTO getById(Long id);
}
