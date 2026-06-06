package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.OrgService;
import com.zenith.admin.dto.data.OrgAddCmd;
import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dto.data.OrgPageQuery;
import com.zenith.admin.dto.data.OrgUpdateCmd;
import com.zenith.admin.service.system.executor.cmd.OrgDeleteCmdExe;
import com.zenith.admin.service.system.executor.qry.OrgGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.OrgListAllQryExe;
import com.zenith.admin.service.system.executor.qry.OrgPageQryExe;
import com.zenith.admin.service.system.executor.cmd.OrgSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.OrgUpdateCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrgServiceImpl implements OrgService {

    private final OrgListAllQryExe orgListAllQryExe;
    private final OrgPageQryExe orgPageQryExe;
    private final OrgSaveCmdExe orgSaveCmdExe;
    private final OrgUpdateCmdExe orgUpdateCmdExe;
    private final OrgDeleteCmdExe orgDeleteCmdExe;
    private final OrgGetByIdQryExe orgGetByIdQryExe;

    @Override
    public List<OrgDTO> listAll() {
        return orgListAllQryExe.execute();
    }

    @Override
    public PageInfo<OrgDTO> page(OrgPageQuery query) {
        return orgPageQryExe.execute(query);
    }

    @Override
    public void save(OrgAddCmd cmd, Long currentUserId) {
        orgSaveCmdExe.execute(cmd);
    }

    @Override
    public void update(OrgUpdateCmd cmd, Long currentUserId) {
        orgUpdateCmdExe.execute(cmd);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        orgDeleteCmdExe.execute(id);
    }

    @Override
    public OrgDTO getById(Long id) {
        return orgGetByIdQryExe.execute(id);
    }
}
