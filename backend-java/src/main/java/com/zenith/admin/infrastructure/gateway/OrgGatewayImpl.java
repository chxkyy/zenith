package com.zenith.admin.infrastructure.gateway;

import com.zenith.admin.domain.gateway.OrgGateway;
import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.infrastructure.convertor.OrgConvertor;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import com.zenith.admin.infrastructure.mapper.OrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrgGatewayImpl implements OrgGateway {

    @Autowired
    private OrgMapper orgMapper;

    @Autowired
    private OrgConvertor orgConvertor;

    @Override
    public List<OrgEntity> listAll() {
        List<OrgDO> orgDOS = orgMapper.selectList(null);
        return orgConvertor.toEntityList(orgDOS);
    }

    @Override
    public void save(OrgEntity org) {
        OrgDO orgDO = orgConvertor.toDataObject(org);
        if (orgDO.getId() == null) {
            orgMapper.insert(orgDO);
        } else {
            orgMapper.updateById(orgDO);
        }
    }

    @Override
    public OrgEntity getById(Long id) {
        OrgDO orgDO = orgMapper.selectById(id);
        return orgConvertor.toEntity(orgDO);
    }

    @Override
    public void deleteById(Long id) {
        orgMapper.deleteById(id);
    }
}
