package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.OrgService;
import com.zenith.admin.dto.data.OrgAddCmd;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
import com.zenith.admin.dto.data.OrgUpdateCmd;
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
        LambdaQueryWrapper<OrgDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(OrgDO::getName, query.getKeyword());
        }

        queryWrapper.orderByAsc(OrgDO::getSort);

        PageInfo<OrgDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> orgMapper.selectList(queryWrapper));
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
    public void save(OrgAddCmd cmd, Long currentUserId) {
        OrgDO orgDO = new OrgDO();
        orgDO.setName(cmd.getName());
        orgDO.setParentId(cmd.getParentId());
        orgDO.setSort(cmd.getSort());
        orgMapper.insert(orgDO);
    }

    @Override
    public void update(OrgUpdateCmd cmd, Long currentUserId) {
        OrgDO orgDO = new OrgDO();
        orgDO.setId(cmd.getId());
        orgDO.setName(cmd.getName());
        orgDO.setParentId(cmd.getParentId());
        orgDO.setSort(cmd.getSort());
        orgMapper.updateById(orgDO);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        orgMapper.deleteById(id);
    }

    @Override
    public OrgDTO getById(Long id) {
        OrgDO orgDO = orgMapper.selectById(id);
        return orgConvertor.toDTO(orgDO);
    }

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
