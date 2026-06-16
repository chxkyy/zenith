package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.FunctionService;
import com.zenith.admin.dto.system.cmd.FunctionAddCmd;
import com.zenith.admin.dto.system.data.FunctionDTO;
import com.zenith.admin.dto.system.qry.FunctionPageQuery;
import com.zenith.admin.dto.system.cmd.FunctionUpdateCmd;
import com.zenith.admin.service.system.executor.cmd.FunctionDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.FunctionSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.FunctionUpdateCmdExe;
import com.zenith.admin.service.system.executor.qry.FunctionGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.FunctionListAllQryExe;
import com.zenith.admin.service.system.executor.qry.FunctionListByMenuIdQryExe;
import com.zenith.admin.service.system.executor.qry.FunctionPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {

    private final FunctionListByMenuIdQryExe functionListByMenuIdQryExe;
    private final FunctionListAllQryExe functionListAllQryExe;
    private final FunctionPageQryExe functionPageQryExe;
    private final FunctionGetByIdQryExe functionGetByIdQryExe;
    private final FunctionSaveCmdExe functionSaveCmdExe;
    private final FunctionUpdateCmdExe functionUpdateCmdExe;
    private final FunctionDeleteCmdExe functionDeleteCmdExe;

    @Override
    public List<FunctionDTO> listByMenuId(Long menuId) {
        return functionListByMenuIdQryExe.execute(menuId);
    }

    @Override
    public List<FunctionDTO> listAll() {
        return functionListAllQryExe.execute();
    }

    @Override
    public PageInfo<FunctionDTO> page(FunctionPageQuery query) {
        return functionPageQryExe.execute(query);
    }

    @Override
    public void save(FunctionAddCmd cmd, Long currentUserId) {
        functionSaveCmdExe.execute(cmd);
    }

    @Override
    public void update(FunctionUpdateCmd cmd, Long currentUserId) {
        functionUpdateCmdExe.execute(cmd);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        functionDeleteCmdExe.execute(id);
    }

    @Override
    public FunctionDTO getById(Long id) {
        return functionGetByIdQryExe.execute(id);
    }
}
