package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.system.data.DictDTO;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictGetByIdQryExe {

    private final DictMapper dictMapper;
    private final DictConvertor dictConvertor;

    public DictDTO execute(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        return dictConvertor.toDTO(dictDO);
    }
}
