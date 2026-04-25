package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.OrgConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
import com.zenith.admin.mapper.OrgMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrgServiceImplTest {

    @Mock
    private OrgMapper orgMapper;

    @Mock
    private OrgConvertor orgConvertor;

    @InjectMocks
    private OrgServiceImpl orgService;

    private OrgDO testOrg;
    private OrgDTO testOrgDTO;

    @BeforeEach
    void setUp() {
        testOrg = new OrgDO();
        testOrg.setId(1L);
        testOrg.setName("研发中心");
        testOrg.setStatus(1);

        testOrgDTO = new OrgDTO();
        testOrgDTO.setId(1L);
        testOrgDTO.setName("研发中心");
    }

    @Test
    @DisplayName("获取所有组织列表")
    void testListAll_Success() {
        when(orgMapper.selectList(null)).thenReturn(Arrays.asList(testOrg));
        when(orgConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testOrgDTO));

        MultiResponse<OrgDTO> result = orgService.listAll();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("分页查询组织列表")
    void testPage_Success() {
        OrgPageQuery query = new OrgPageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(orgMapper.selectList(any())).thenReturn(Arrays.asList(testOrg));
        when(orgConvertor.toDataObject(any(OrgDTO.class))).thenReturn(testOrg);
        when(orgConvertor.toDTO(any(OrgDO.class))).thenReturn(testOrgDTO);
        when(orgConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testOrgDTO));

        PageInfo<OrgDTO> result = orgService.page(query);

        assertNotNull(result);
        verify(orgMapper).selectList(any());
    }

    @Test
    @DisplayName("保存组织")
    void testSave_Org() {
        when(orgConvertor.toDataObject(any(OrgDTO.class))).thenReturn(testOrg);

        orgService.save(testOrgDTO);

        verify(orgMapper).updateById(any(OrgDO.class));
    }

    @Test
    @DisplayName("更新组织信息")
    void testUpdate_Org() {
        when(orgConvertor.toDataObject(any(OrgDTO.class))).thenReturn(testOrg);

        orgService.update(testOrgDTO);

        verify(orgMapper).updateById(any(OrgDO.class));
    }

    @Test
    @DisplayName("根据ID获取组织")
    void testGetById_Success() {
        when(orgMapper.selectById(1L)).thenReturn(testOrg);
        when(orgConvertor.toDTO(any(OrgDO.class))).thenReturn(testOrgDTO);

        OrgDTO result = orgService.getById(1L);

        assertNotNull(result);
        assertEquals("研发中心", result.getName());
    }

    @Test
    @DisplayName("删除组织")
    void testDelete_Success() {
        orgService.delete(1L);

        verify(orgMapper).deleteById(1L);
    }
}
