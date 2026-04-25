package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.FileConvertor;
import com.zenith.admin.dataobject.FileDO;
import com.zenith.admin.dto.data.FileDTO;
import com.zenith.admin.dto.data.FilePageQuery;
import com.zenith.admin.mapper.FileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileServiceImplTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FileConvertor fileConvertor;

    @InjectMocks
    private FileServiceImpl fileService;

    private FileDO testFile;
    private FileDTO testFileDTO;

    @BeforeEach
    void setUp() {
        testFile = new FileDO();
        testFile.setId(1L);
        testFile.setName("uuid-test.pdf");
        testFile.setOriginalName("test.pdf");
        testFile.setPath("/files/uuid-test.pdf");
        testFile.setSize(1024L);
        testFile.setType("application/pdf");

        testFileDTO = new FileDTO();
        testFileDTO.setId(1L);
        testFileDTO.setOriginalName("test.pdf");
        testFileDTO.setPath("/files/uuid-test.pdf");
    }

    @Test
    @DisplayName("分页查询文件列表")
    void testPage_Success() {
        FilePageQuery query = new FilePageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(fileMapper.selectList(any())).thenReturn(Arrays.asList(testFile));
        when(fileConvertor.toDataObject(any(FileDTO.class))).thenReturn(testFile);
        when(fileConvertor.toDTO(any(FileDO.class))).thenReturn(testFileDTO);
        when(fileConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testFileDTO));

        PageInfo<FileDTO> result = fileService.page(query);

        assertNotNull(result);
        verify(fileMapper).selectList(any());
    }

    @Test
    @DisplayName("上传文件成功")
    void testUpload_Success() throws IOException {
        byte[] content = "test file content".getBytes();
        when(fileConvertor.toDataObject(any(FileDTO.class))).thenReturn(testFile);
        when(fileConvertor.toDTO(any(FileDO.class))).thenReturn(testFileDTO);

        FileDTO result = fileService.upload(content, "test.pdf", "application/pdf", 1024L);

        assertNotNull(result);
        assertEquals("test.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("根据ID获取文件")
    void testGetById_Success() {
        when(fileMapper.selectById(1L)).thenReturn(testFile);
        when(fileConvertor.toDTO(any(FileDO.class))).thenReturn(testFileDTO);

        FileDTO result = fileService.getById(1L);

        assertNotNull(result);
        assertEquals("test.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("删除文件")
    void testDelete_Success() {
        when(fileMapper.selectById(1L)).thenReturn(testFile);

        fileService.delete(1L);

        verify(fileMapper).selectById(1L);
    }
}
