package com.zenith.admin.service.system.executor.qry;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.service.system.executor.converter.OrgConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dto.system.data.OrgDTO;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgGetByIdQryExe {

    private final OrgMapper orgMapper;
    private final OrgConvertor orgConvertor;

    public OrgDTO execute(Long id) {
        if (id == null) {
            throw new BizException("ORG_002", "组织ID不能为空");
        }
        OrgDO orgDO = orgMapper.selectById(id);
        if (orgDO == null) {
            throw new BizException("ORG_003", "组织不存在");
        }
        return orgConvertor.toDTO(orgDO);
    }
}
