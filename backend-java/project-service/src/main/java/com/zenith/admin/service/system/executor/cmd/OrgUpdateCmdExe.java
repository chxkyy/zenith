package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.data.OrgUpdateCmd;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgUpdateCmdExe {

    private final OrgMapper orgMapper;

    public void execute(OrgUpdateCmd cmd) {
        OrgDO orgDO = new OrgDO();
        orgDO.setId(cmd.getId());
        orgDO.setName(cmd.getName());
        orgDO.setParentId(cmd.getParentId());
        orgDO.setSort(cmd.getSort());
        orgMapper.updateById(orgDO);
    }
}
