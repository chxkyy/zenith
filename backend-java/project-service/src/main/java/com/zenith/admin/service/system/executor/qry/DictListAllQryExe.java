package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DictListAllQryExe {

    private final DictMapper dictMapper;
    private final DictConvertor dictConvertor;

    public List<DictDTO> execute() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        return dictConvertor.toDTOList(dictDOS);
    }
}
