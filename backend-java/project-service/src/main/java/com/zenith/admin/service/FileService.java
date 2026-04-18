package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.dataobject.FileDTO;
import com.zenith.admin.dto.dataobject.FilePageQuery;
import com.zenith.admin.FileConvertor;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;
    private final FileConvertor fileConvertor;

    private String getUploadDir() {
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "uploads" + File.separator;
    }

    public PageInfo<FileDTO> page(FilePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<FileDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(FileDO::getName, query.getKeyword());
        }

        queryWrapper.orderByDesc(FileDO::getCreatedAt);
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

    public FileDTO upload(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String uploadDir = getUploadDir();
        String path = uploadDir + fileName;

        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        File destFile = new File(path);
        file.transferTo(destFile);

        FileDO fileDO = new FileDO();
        fileDO.setName(fileName);
        fileDO.setOriginalName(originalName);
        fileDO.setPath("/" + path);
        fileDO.setType(ext.replace(".", "").toUpperCase());
        fileDO.setSize(file.getSize());
        fileDO.setUploader("admin");
        fileDO.setCreatedAt(LocalDateTime.now());

        fileMapper.insert(fileDO);

        return fileConvertor.toDTO(fileDO);
    }

    public void delete(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        if (fileDO != null) {
            File file = new File(fileDO.getPath().substring(1));
            if (file.exists()) {
                file.delete();
            }
            fileMapper.deleteById(id);
        }
    }

    public FileDTO getById(Long id) {
        FileDO fileDO = fileMapper.selectById(id);
        return fileConvertor.toDTO(fileDO);
    }
}
