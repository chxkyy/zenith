package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.system.data.DictDTO;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DictListByTypeQryExe {

    private final DictMapper dictMapper;
    private final DictConvertor dictConvertor;

    public List<DictDTO> execute(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        return dictConvertor.toDTOList(dictDOS);
    }
}
