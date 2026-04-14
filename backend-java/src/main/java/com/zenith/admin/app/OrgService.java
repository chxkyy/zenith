package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.dto.OrgDTO;
import com.zenith.admin.dto.OrgPageQuery;
import com.zenith.admin.infrastructure.convertor.OrgConvertor;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import com.zenith.admin.infrastructure.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    public MultiResponse<OrgDTO> listAll() {
        List<OrgDO> orgDOS = orgMapper.selectList(null);
        List<OrgEntity> entities = orgConvertor.toEntityList(orgDOS);
        List<OrgDTO> dtos = orgConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public PageResponse<OrgDTO> page(OrgPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<OrgDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(OrgDO::getName, query.getKeyword());
        }

        queryWrapper.orderByAsc(OrgDO::getSort);
        List<OrgDO> orgDOS = orgMapper.selectList(queryWrapper);
        PageInfo<OrgDO> pageInfo = new PageInfo<>(orgDOS);
        List<OrgEntity> entities = orgConvertor.toEntityList(pageInfo.getList());
        List<OrgDTO> dtos = orgConvertor.toDTOList(entities);
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(OrgDTO orgDTO) {
        OrgEntity entity = orgConvertor.toEntity(orgDTO);
        OrgDO orgDO = orgConvertor.toDataObject(entity);
        if (orgDO.getId() == null) {
            orgMapper.insert(orgDO);
        } else {
            orgMapper.updateById(orgDO);
        }
    }

    public void update(OrgDTO orgDTO) {
        OrgEntity entity = orgConvertor.toEntity(orgDTO);
        OrgDO orgDO = orgConvertor.toDataObject(entity);
        orgMapper.updateById(orgDO);
    }

    public void delete(Long id) {
        orgMapper.deleteById(id);
    }

    public OrgDTO getById(Long id) {
        OrgDO orgDO = orgMapper.selectById(id);
        OrgEntity entity = orgConvertor.toEntity(orgDO);
        return orgConvertor.toDTO(entity);
    }
}
