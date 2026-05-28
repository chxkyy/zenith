package com.zenith.admin.executor;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.mapper.OrgMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgDeleteCmdExe {

    private final OrgMapper orgMapper;

    public void execute(Long id) {
        if (id == null) {
            throw new BizException("ORG_001", "组织ID不能为空");
        }
        orgMapper.deleteById(id);
    }
}
