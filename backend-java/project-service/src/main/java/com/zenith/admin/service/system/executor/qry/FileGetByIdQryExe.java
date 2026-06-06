package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.service.system.executor.converter.FileConvertor;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileGetByIdQryExe {

    private final FileMapper fileMapper;
    private final FileConvertor fileConvertor;

    public FileDTO execute(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        return fileConvertor.toDTO(fileDO);
    }
}
