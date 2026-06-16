package com.zenith.admin.service.inst.executor.cmd;

import com.zenith.admin.dataobject.InstProductDO;
import com.zenith.admin.mapper.InstProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 删除产品执行器
 */
@Component
@RequiredArgsConstructor
public class InstProductDeleteCmdExe {

    private final InstProductMapper instProductMapper;

    /**
     * 执行删除操作（物理删除）
     *
     * @param id 产品ID
     * @return 是否成功
     */
    public boolean execute(Long id) {
        // 校验存在性
        InstProductDO existingDO = instProductMapper.selectById(id);
        if (existingDO == null) {
            throw new RuntimeException("产品不存在");
        }

        // 物理删除
        int deleted = instProductMapper.deleteById(id);
        return deleted > 0;
    }
}
