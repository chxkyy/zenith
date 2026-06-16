package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.data.FileDTO;
import com.zenith.admin.service.system.executor.converter.FileConvertor;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadCmdExe {

    private final FileMapper fileMapper;
    private final FileConvertor fileConvertor;

    private String getUploadDir() {
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "uploads" + File.separator;
    }

    public FileDTO execute(byte[] fileContent, String originalName, String contentType, long fileSize) throws IOException {
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String uploadDir = getUploadDir();
        String relativePath = "uploads" + File.separator + fileName;
        String fullPath = uploadDir + fileName;

        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(fileContent);
        }

        FileDO fileDO = new FileDO();
        fileDO.setName(fileName);
        fileDO.setOriginalName(originalName);
        fileDO.setPath("/" + relativePath.replace("\\", "/"));
        fileDO.setType(ext.replace(".", "").toUpperCase());
        fileDO.setSize(fileSize);
        fileDO.setUploader("admin");
        fileMapper.insert(fileDO);

        return fileConvertor.toDTO(fileDO);
    }
}
