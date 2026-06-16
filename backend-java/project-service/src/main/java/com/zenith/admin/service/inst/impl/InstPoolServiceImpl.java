package com.zenith.admin.service.inst.impl;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.inst.InstPoolService;
import com.zenith.admin.dto.inst.cmd.InstPoolCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolStatusCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolUpdateCmd;
import com.zenith.admin.dto.inst.data.InstPoolDTO;
import com.zenith.admin.dto.inst.qry.InstPoolPageQuery;
import com.zenith.admin.service.inst.executor.cmd.InstPoolCreateCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstPoolDeleteIfEmptyCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstPoolToggleStatusCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstPoolUpdateCmdExe;
import com.zenith.admin.service.inst.executor.qry.InstPoolDetailQryExe;
import com.zenith.admin.service.inst.executor.qry.InstPoolListQryExe;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 机构池服务实现类
 */
@Service
@RequiredArgsConstructor
public class InstPoolServiceImpl implements InstPoolService {

    private final InstPoolCreateCmdExe createCmdExe;
    private final InstPoolUpdateCmdExe updateCmdExe;
    private final InstPoolToggleStatusCmdExe toggleStatusCmdExe;
    private final InstPoolDeleteIfEmptyCmdExe deleteIfEmptyCmdExe;
    private final InstPoolListQryExe listQryExe;
    private final InstPoolDetailQryExe detailQryExe;

    @Override
    public SingleResponse<Long> create(InstPoolCreateCmd cmd) {
        Long id = createCmdExe.execute(cmd);
        return SingleResponse.of(id);
    }

    @Override
    public SingleResponse<Boolean> update(InstPoolUpdateCmd cmd) {
        updateCmdExe.execute(cmd);
        return SingleResponse.of(true);
    }

    @Override
    public SingleResponse<Boolean> toggleStatus(InstPoolStatusCmd cmd) {
        toggleStatusCmdExe.execute(cmd);
        return SingleResponse.of(true);
    }

    @Override
    public SingleResponse<Boolean> deleteIfEmpty(Long poolId) {
        boolean result = deleteIfEmptyCmdExe.execute(poolId);
        return SingleResponse.of(result);
    }

    @Override
    public PageResponse<InstPoolDTO> page(InstPoolPageQuery query) {
        PageInfo<InstPoolDTO> pageInfo = listQryExe.execute(query);
        return PageResponseUtils.of(pageInfo);
    }

    @Override
    public SingleResponse<InstPoolDTO> detail(Long id) {
        return detailQryExe.execute(id);
    }
}
