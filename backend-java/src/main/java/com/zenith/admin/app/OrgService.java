package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.gateway.OrgGateway;
import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.dto.OrgDTO;
import com.zenith.admin.infrastructure.convertor.OrgConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgService {

    @Autowired
    private OrgGateway orgGateway;

    @Autowired
    private OrgConvertor orgConvertor;

    public MultiResponse<OrgDTO> listAll() {
        List<OrgEntity> entities = orgGateway.listAll();
        List<OrgDTO> dtos = orgConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(OrgDTO orgDTO) {
        OrgEntity entity = orgConvertor.toEntity(orgDTO);
        orgGateway.save(entity);
    }

    public void update(OrgDTO orgDTO) {
        OrgEntity entity = orgConvertor.toEntity(orgDTO);
        orgGateway.save(entity);
    }

    public void delete(Long id) {
        orgGateway.deleteById(id);
    }

    public OrgDTO getById(Long id) {
        OrgEntity entity = orgGateway.getById(id);
        return orgConvertor.toDTO(entity);
    }
}
