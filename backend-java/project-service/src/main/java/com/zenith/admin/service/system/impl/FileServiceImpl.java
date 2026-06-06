package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.FileService;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.data.FilePageQuery;
import com.zenith.admin.service.system.executor.cmd.FileDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.FileUploadCmdExe;
import com.zenith.admin.service.system.executor.qry.FileGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.FilePageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FilePageQryExe filePageQryExe;
    private final FileGetByIdQryExe fileGetByIdQryExe;
    private final FileUploadCmdExe fileUploadCmdExe;
    private final FileDeleteCmdExe fileDeleteCmdExe;

    @Override
    public PageInfo<FileDTO> page(FilePageQuery query) {
        return filePageQryExe.execute(query);
    }

    @Override
    public FileDTO upload(byte[] fileContent, String originalFilename, String contentType, long fileSize, Long currentUserId) throws IOException {
        return fileUploadCmdExe.execute(fileContent, originalFilename, contentType, fileSize);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        fileDeleteCmdExe.execute(id);
    }

    @Override
    public FileDTO getById(Long id) {
        return fileGetByIdQryExe.execute(id);
    }
}
