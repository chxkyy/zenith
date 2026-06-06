package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.cmd.DictItemUpdateCmd;
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
