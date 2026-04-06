package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.infrastructure.convertor.DictConvertor;
import com.zenith.admin.infrastructure.dataobject.DictDO;
import com.zenith.admin.infrastructure.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictService {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictConvertor dictConvertor;

    public MultiResponse<DictDTO> listAll() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        List<DictEntity> entities = dictConvertor.toEntityList(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public MultiResponse<DictDTO> listByType(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type).orderByAsc(DictDO::getSort);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        List<DictEntity> entities = dictConvertor.toEntityList(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(DictDTO dictDTO) {
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        DictDO dictDO = dictConvertor.toDataObject(entity);
        if (dictDO.getId() == null) {
            dictMapper.insert(dictDO);
        } else {
            dictMapper.updateById(dictDO);
        }
    }

    public void update(DictDTO dictDTO) {
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        DictDO dictDO = dictConvertor.toDataObject(entity);
        dictMapper.updateById(dictDO);
    }

    public void delete(Long id) {
        dictMapper.deleteById(id);
    }

    public DictDTO getById(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        DictEntity entity = dictConvertor.toEntity(dictDO);
        return dictConvertor.toDTO(entity);
    }
}
