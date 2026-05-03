package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.OrgService;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
import com.zenith.admin.OrgConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    @Override
    public List<OrgDTO> listAll() {
        List<OrgDO> orgDOS = orgMapper.selectList(null);
        List<OrgDTO> dtos = orgConvertor.toDTOList(orgDOS);

        for (OrgDTO dto : dtos) {
            List<Long> allOrgIds = getAllChildOrgIds(dto.getId());
            allOrgIds.add(dto.getId());
            Integer memberCount = orgMapper.countMembersByOrgIds(allOrgIds);
            dto.setMemberCount(memberCount);
        }

        return dtos;
    }

    @Override
    public PageInfo<OrgDTO> page(OrgPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<OrgDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(OrgDO::getName, query.getKeyword());
        }

        queryWrapper.orderByAsc(OrgDO::getSort);
        List<OrgDO> orgDOS = orgMapper.selectList(queryWrapper);
        PageInfo<OrgDO> pageInfo = new PageInfo<>(orgDOS);
        List<OrgDTO> dtos = orgConvertor.toDTOList(pageInfo.getList());

        for (OrgDTO dto : dtos) {
            List<Long> allOrgIds = getAllChildOrgIds(dto.getId());
            allOrgIds.add(dto.getId());
            Integer memberCount = orgMapper.countMembersByOrgIds(allOrgIds);
            dto.setMemberCount(memberCount);
        }

        PageInfo<OrgDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void save(OrgDTO orgDTO) {
        OrgDO orgDO = orgConvertor.toDataObject(orgDTO);
        Long currentUserId = 1L;
        if (orgDO.getId() == null) {
            orgDO.setCreateUserId(currentUserId);
            orgDO.setCreatedTime(java.time.LocalDateTime.now());
            orgMapper.insert(orgDO);
        } else {
            orgDO.setUpdateUserId(currentUserId);
            orgDO.setUpdateTime(java.time.LocalDateTime.now());
            orgMapper.updateById(orgDO);
        }
    }

    @Override
    public void update(OrgDTO orgDTO) {
        OrgDO orgDO = orgConvertor.toDataObject(orgDTO);
        Long currentUserId = 1L;
        orgDO.setUpdateUserId(currentUserId);
        orgDO.setUpdateTime(java.time.LocalDateTime.now());
        orgMapper.updateById(orgDO);
    }

    @Override
    public void delete(Long id) {
        orgMapper.deleteById(id);
    }

    @Override
    public OrgDTO getById(Long id) {
        OrgDO orgDO = orgMapper.selectById(id);
        return orgConvertor.toDTO(orgDO);
    }

    /**
     * 递归获取指定组织的所有子组织ID（不包含自身）
     * @param parentId 父组织ID
     * @return 所有子组织ID列表
     */
    private List<Long> getAllChildOrgIds(Long parentId) {
        List<Long> ids = new ArrayList<>();
        LambdaQueryWrapper<OrgDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgDO::getParentId, parentId);
        List<OrgDO> children = orgMapper.selectList(wrapper);

        for (OrgDO child : children) {
            ids.add(child.getId());
            ids.addAll(getAllChildOrgIds(child.getId()));
        }
        return ids;
    }
}
