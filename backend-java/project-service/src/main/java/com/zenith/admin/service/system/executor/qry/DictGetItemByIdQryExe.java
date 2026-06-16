package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.system.data.DictItemDTO;
import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictGetItemByIdQryExe {

    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

    public DictItemDTO execute(Long id) {
        DictItemDO dictItemDO = dictItemMapper.selectById(id);
        return dictConvertor.toItemDTO(dictItemDO);
    }
}
