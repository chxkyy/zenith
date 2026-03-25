package com.zenith.admin.infrastructure.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.domain.gateway.DictGateway;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.infrastructure.convertor.DictConvertor;
import com.zenith.admin.infrastructure.dataobject.DictDO;
import com.zenith.admin.infrastructure.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DictGatewayImpl implements DictGateway {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictConvertor dictConvertor;

    @Override
    public List<DictEntity> listAll() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        return dictConvertor.toEntityList(dictDOS);
    }

    @Override
    public List<DictEntity> listByType(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type).orderByAsc(DictDO::getSort);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        return dictConvertor.toEntityList(dictDOS);
    }

    @Override
    public void save(DictEntity dict) {
        DictDO dictDO = dictConvertor.toDataObject(dict);
        if (dictDO.getId() == null) {
            dictMapper.insert(dictDO);
        } else {
            dictMapper.updateById(dictDO);
        }
    }

    @Override
    public DictEntity getById(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        return dictConvertor.toEntity(dictDO);
    }

    @Override
    public void deleteById(Long id) {
        dictMapper.deleteById(id);
    }
}
