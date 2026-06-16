package com.zenith.admin.service.inst.impl;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.inst.InstInstitutionService;
import com.zenith.admin.dto.inst.cmd.InstInstitutionCreateCmd;
import com.zenith.admin.dto.inst.cmd.InstPoolAddInstitutionCmd;
import com.zenith.admin.dto.inst.data.InstInstitutionDTO;
import com.zenith.admin.dto.inst.data.SimpleInstitutionDTO;
import com.zenith.admin.dto.inst.qry.InstitutionPageQuery;
import com.zenith.admin.service.inst.executor.cmd.InstAddToPoolCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstInstitutionCreateCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstRemoveFromPoolCmdExe;
import com.zenith.admin.service.inst.executor.qry.InstInstitutionDetailQryExe;
import com.zenith.admin.service.inst.executor.qry.InstInstitutionListQryExe;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 合作机构服务实现类
 */
@Service
@RequiredArgsConstructor
public class InstInstitutionServiceImpl implements InstInstitutionService {

    private final InstInstitutionCreateCmdExe createCmdExe;
    private final InstInstitutionListQryExe listQryExe;
    private final InstInstitutionDetailQryExe detailQryExe;
    private final InstAddToPoolCmdExe addToPoolCmdExe;
    private final InstRemoveFromPoolCmdExe removeFromPoolCmdExe;

    @Override
    public SingleResponse<Long> create(InstInstitutionCreateCmd cmd) {
        Long id = createCmdExe.execute(cmd);
        return SingleResponse.of(id);
    }

    @Override
    public SingleResponse<Boolean> update(InstInstitutionCreateCmd cmd) {
        createCmdExe.execute(cmd);
        return SingleResponse.of(true);
    }

    @Override
    public PageResponse<SimpleInstitutionDTO> page(InstitutionPageQuery query) {
        PageInfo<SimpleInstitutionDTO> pageInfo = listQryExe.execute(query);
        return PageResponseUtils.of(pageInfo);
    }

    @Override
    public SingleResponse<InstInstitutionDTO> detail(Long id) {
        return detailQryExe.execute(id);
    }

    @Override
    public SingleResponse<Void> addToPool(InstPoolAddInstitutionCmd cmd) {
        addToPoolCmdExe.execute(cmd);
        return SingleResponse.buildSuccess();
    }

    @Override
    public SingleResponse<Void> removeFromPool(Long poolId, Long instId) {
        removeFromPoolCmdExe.execute(poolId, instId);
        return SingleResponse.buildSuccess();
    }
}
