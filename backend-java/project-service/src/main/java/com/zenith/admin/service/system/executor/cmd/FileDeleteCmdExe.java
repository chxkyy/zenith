package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FileDeleteCmdExe {

    private final FileMapper fileMapper;

    public void execute(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (fileDO != null) {
            String path = fileDO.getPath();
            if (path != null && path.startsWith("/")) {
                path = path.substring(1);
            }
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            fileMapper.deleteById(id);
        }
    }
}
