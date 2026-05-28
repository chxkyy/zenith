package com.zenith.admin.executor;

import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictItemDTO;
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
