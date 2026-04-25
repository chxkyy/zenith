package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.NoticeService;
import com.zenith.admin.NoticeConvertor;
import com.zenith.admin.dataobject.NoticeDO;
import com.zenith.admin.dto.data.NoticeDTO;
import com.zenith.admin.dto.data.NoticePageQuery;
import com.zenith.admin.mapper.NoticeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeMapper noticeMapper;

    @Mock
    private NoticeConvertor noticeConvertor;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private NoticeDO testNotice;
    private NoticeDTO testNoticeDTO;

    @BeforeEach
    void setUp() {
        testNotice = new NoticeDO();
        testNotice.setId(1L);
        testNotice.setTitle("系统维护通知");
        testNotice.setContent("系统将于今晚进行维护");
        testNotice.setStatus("draft");

        testNoticeDTO = new NoticeDTO();
        testNoticeDTO.setId(1L);
        testNoticeDTO.setTitle("系统维护通知");
        testNoticeDTO.setContent("系统将于今晚进行维护");
        testNoticeDTO.setStatus("draft");
    }

    @Test
    @DisplayName("获取所有公告列表")
    void testListAll_Success() {
        when(noticeMapper.selectList(null)).thenReturn(Arrays.asList(testNotice));
        when(noticeConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testNoticeDTO));

        MultiResponse<NoticeDTO> result = noticeService.listAll();

        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("分页查询公告列表")
    void testPage_Success() {
        NoticePageQuery query = new NoticePageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(noticeMapper.selectList(any())).thenReturn(Arrays.asList(testNotice));
        when(noticeConvertor.toDataObject(any(NoticeDTO.class))).thenReturn(testNotice);
        when(noticeConvertor.toDTO(any(NoticeDO.class))).thenReturn(testNoticeDTO);
        when(noticeConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testNoticeDTO));

        PageInfo<NoticeDTO> result = noticeService.page(query);

        assertNotNull(result);
        verify(noticeMapper).selectList(any());
    }

    @Test
    @DisplayName("保存新公告")
    void testSave_NewNotice() {
        when(noticeConvertor.toDataObject(any(NoticeDTO.class))).thenReturn(testNotice);

        noticeService.save(testNoticeDTO);

        verify(noticeMapper).insert(any(NoticeDO.class));
    }

    @Test
    @DisplayName("根据ID获取公告")
    void testGetById_Success() {
        when(noticeMapper.selectById(1L)).thenReturn(testNotice);
        when(noticeConvertor.toDTO(any(NoticeDO.class))).thenReturn(testNoticeDTO);

        NoticeDTO result = noticeService.getById(1L);

        assertNotNull(result);
        assertEquals("系统维护通知", result.getTitle());
    }

    @Test
    @DisplayName("删除公告")
    void testDelete_Success() {
        noticeService.delete(1L);

        verify(noticeMapper).deleteById(1L);
    }

    @Test
    @DisplayName("更新公告状态")
    void testUpdateStatus_Success() {
        when(noticeMapper.selectById(1L)).thenReturn(testNotice);

        noticeService.updateStatus(1L, "published");

        verify(noticeMapper).updateById(argThat(notice -> "published".equals(notice.getStatus())));
    }
}
