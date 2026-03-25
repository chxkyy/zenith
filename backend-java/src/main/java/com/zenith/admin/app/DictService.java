package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.gateway.DictGateway;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.infrastructure.convertor.DictConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictService {

    @Autowired
    private DictGateway dictGateway;

    @Autowired
    private DictConvertor dictConvertor;

    public MultiResponse<DictDTO> listAll() {
        List<DictEntity> entities = dictGateway.listAll();
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public MultiResponse<DictDTO> listByType(String type) {
        List<DictEntity> entities = dictGateway.listByType(type);
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(DictDTO dictDTO) {
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        dictGateway.save(entity);
    }

    public void update(DictDTO dictDTO) {
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        dictGateway.save(entity);
    }

    public void delete(Long id) {
        dictGateway.deleteById(id);
    }

    public DictDTO getById(Long id) {
        DictEntity entity = dictGateway.getById(id);
        return dictConvertor.toDTO(entity);
    }
}
