package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.FileService;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.data.FilePageQuery;
import com.zenith.admin.FileConvertor;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final FileConvertor fileConvertor;

    private String getUploadDir() {
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "uploads" + File.separator;
    }

    @Override
    public PageInfo<FileDTO> page(FilePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<FileDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(FileDO::getName, query.getKeyword());
        }

        queryWrapper.orderByDesc(FileDO::getCreatedTime);
        List<FileDO> fileDOS = fileMapper.selectList(queryWrapper);
        PageInfo<FileDO> pageInfo = new PageInfo<>(fileDOS);
        List<FileDTO> fileDTOS = fileConvertor.toDTOList(pageInfo.getList());

        PageInfo<FileDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(fileDTOS);
        return result;
    }

    @Override
    public FileDTO upload(byte[] fileContent, String originalName, String contentType, long fileSize, Long currentUserId) throws IOException {
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

    @Override
    public void delete(Long id, Long currentUserId) {
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

    @Override
    public FileDTO getById(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        return fileConvertor.toDTO(fileDO);
    }
}
