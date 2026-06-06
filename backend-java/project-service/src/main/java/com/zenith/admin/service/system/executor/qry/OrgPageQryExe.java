package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.service.system.executor.converter.OrgConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.query.OrgPageQuery;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrgPageQryExe {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    public PageInfo<OrgDTO> execute(OrgPageQuery query) {
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
