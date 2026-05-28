package com.zenith.admin.executor;

import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictItemUpdateCmd;
import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictUpdateItemCmdExe {

    private final DictItemMapper dictItemMapper;

    public void execute(DictItemUpdateCmd cmd) {
        DictItemDO dictItemDO = new DictItemDO();
        dictItemDO.setId(cmd.getId());
        dictItemDO.setType(cmd.getType());
        dictItemDO.setLabel(cmd.getLabel());
        dictItemDO.setDictValue(cmd.getDictValue());
        dictItemDO.setSort(cmd.getSort());
        dictItemMapper.updateById(dictItemDO);
    }
}
