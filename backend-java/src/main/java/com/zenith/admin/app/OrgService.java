package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.dto.OrgDTO;
import com.zenith.admin.infrastructure.convertor.OrgConvertor;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import com.zenith.admin.infrastructure.mapper.OrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgService {

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private OrgConvertor orgConvertor;

    public MultiResponse<OrgDTO> listAll() {
        List<OrgDO> orgDOS = orgMapper.selectList(null);
        List<OrgEntity> entities = orgConvertor.toEntityList(orgDOS);
        List<OrgDTO> dtos = orgConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
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
