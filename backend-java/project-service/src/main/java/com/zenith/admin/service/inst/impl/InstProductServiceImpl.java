package com.zenith.admin.service.inst.impl;

import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.inst.InstProductService;
import com.zenith.admin.dto.inst.cmd.InstProductCreateCmd;
import com.zenith.admin.dto.inst.data.InstProductDTO;
import com.zenith.admin.dto.inst.qry.InstProductPageQuery;
import com.zenith.admin.service.inst.executor.cmd.InstProductCreateCmdExe;
import com.zenith.admin.service.inst.executor.cmd.InstProductDeleteCmdExe;
import com.zenith.admin.service.inst.executor.qry.InstProductListQryExe;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 产品服务实现类
 */
@Service
@RequiredArgsConstructor
public class InstProductServiceImpl implements InstProductService {

    private final InstProductCreateCmdExe createCmdExe;
    private final InstProductDeleteCmdExe deleteCmdExe;
    private final InstProductListQryExe listQryExe;

    @Override
    public SingleResponse<Long> create(InstProductCreateCmd cmd) {
        Long id = createCmdExe.execute(cmd);
        return SingleResponse.of(id);
    }

    @Override
    public SingleResponse<Boolean> update(InstProductCreateCmd cmd) {
        createCmdExe.execute(cmd);
        return SingleResponse.of(true);
    }

    @Override
    public SingleResponse<Boolean> delete(Long id) {
        boolean result = deleteCmdExe.execute(id);
        return SingleResponse.of(result);
    }

    @Override
    public PageResponse<InstProductDTO> page(InstProductPageQuery query) {
        PageInfo<InstProductDTO> pageInfo = listQryExe.execute(query);
        return PageResponseUtils.of(pageInfo);
    }
}
