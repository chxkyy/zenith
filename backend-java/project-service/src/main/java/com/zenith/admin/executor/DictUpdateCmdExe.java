package com.zenith.admin.executor;

import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.data.DictUpdateCmd;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictUpdateCmdExe {

    private final DictMapper dictMapper;

    public void execute(DictUpdateCmd cmd) {
        DictDO dictDO = new DictDO();
        dictDO.setId(cmd.getId());
        dictDO.setName(cmd.getName());
        dictDO.setType(cmd.getType());
        dictMapper.updateById(dictDO);
    }
}
