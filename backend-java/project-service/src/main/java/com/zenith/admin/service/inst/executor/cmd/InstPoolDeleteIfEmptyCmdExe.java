package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.mapper.InstPoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 删除空池执行器（仅当池内无机构时允许删除）
 */
@Component
@RequiredArgsConstructor
public class InstPoolDeleteIfEmptyCmdExe {

    private final InstPoolMapper instPoolMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行删除空池操作
     *
     * @param poolId 池ID
     * @return 是否成功
     */
    public boolean execute(Long poolId) {
        // 校验池是否存在
        if (instPoolMapper.selectById(poolId) == null) {
            throw new RuntimeException("机构池不存在");
        }

        // 统计池内机构数，=0才允许删除
        LambdaQueryWrapper<InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolInstitutionDO::getPoolId, poolId);
        Long institutionCount = instPoolInstitutionMapper.selectCount(wrapper);
        if (institutionCount != null && institutionCount > 0) {
            throw new RuntimeException("该机构池下存在关联机构，无法删除");
        }

        // 物理删除
        instPoolMapper.deleteById(poolId);
        return true;
    }
}
