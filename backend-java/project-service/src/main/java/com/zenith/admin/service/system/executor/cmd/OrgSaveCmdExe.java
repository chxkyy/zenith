package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.cmd.OrgAddCmd;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgSaveCmdExe {

    private final OrgMapper orgMapper;

    public void execute(OrgAddCmd cmd) {
        OrgDO orgDO = new OrgDO();
        orgDO.setName(cmd.getName());
        orgDO.setParentId(cmd.getParentId());
        orgDO.setSort(cmd.getSort());
        orgMapper.insert(orgDO);
    }
}
