package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.cmd.FunctionAddCmd;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FunctionSaveCmdExe {

    private final FunctionMapper functionMapper;

    public void execute(FunctionAddCmd cmd) {
        FunctionDO functionDO = new FunctionDO();
        functionDO.setName(cmd.getName());
        functionDO.setType(cmd.getType());
        functionDO.setMenuId(cmd.getMenuId());
        functionDO.setPermission(cmd.getPermission());
        functionDO.setSort(cmd.getSort());
        functionDO.setStatus(cmd.getStatus());
        functionMapper.insert(functionDO);
    }
}
