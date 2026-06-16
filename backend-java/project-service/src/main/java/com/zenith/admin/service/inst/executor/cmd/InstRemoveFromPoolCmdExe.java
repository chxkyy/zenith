package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 机构移出池执行器
 */
@Component
@RequiredArgsConstructor
public class InstRemoveFromPoolCmdExe {

    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行移出操作
     *
     * @param poolId 池ID
     * @param instId 机构ID
     */
    public void execute(Long poolId, Long instId) {
        // 删除关联记录
        LambdaQueryWrapper<InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolInstitutionDO::getPoolId, poolId)
               .eq(InstPoolInstitutionDO::getInstitutionId, instId);

        int deleted = instPoolInstitutionMapper.delete(wrapper);
        if (deleted == 0) {
            throw new RuntimeException("该机构不在此机构池中");
        }
    }
}
