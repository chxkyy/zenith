package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.OrgConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrgListAllQryExe {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    public List<OrgDTO> execute() {
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
