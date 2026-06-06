package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.data.FunctionUpdateCmd;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FunctionUpdateCmdExe {

    private final FunctionMapper functionMapper;

    public void execute(FunctionUpdateCmd cmd) {
        FunctionDO functionDO = new FunctionDO();
        functionDO.setId(cmd.getId());
        functionDO.setName(cmd.getName());
        functionDO.setType(cmd.getType());
        functionDO.setMenuId(cmd.getMenuId());
        functionDO.setPermission(cmd.getPermission());
        functionDO.setSort(cmd.getSort());
        functionDO.setStatus(cmd.getStatus());
        functionMapper.updateById(functionDO);
    }
}
