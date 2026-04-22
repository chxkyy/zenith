package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    @Override
    public MultiResponse<OrgDTO> listAll() {
        List<OrgDO> orgDOS = orgMapper.selectList(null);
        List<OrgDTO> dtos = orgConvertor.toDTOList(orgDOS);
        return MultiResponse.of(dtos);
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
        if (orgDO.getId() == null) {
            orgMapper.insert(orgDO);
        } else {
            orgMapper.updateById(orgDO);
        }
    }

    @Override
    public void update(OrgDTO orgDTO) {
        OrgDO orgDO = orgConvertor.toDataObject(orgDTO);
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
}
