package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.dto.inst.cmd.InstPoolStatusCmd;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.mapper.InstPoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 机构池启用/停用执行器
 */
@Component
@RequiredArgsConstructor
public class InstPoolToggleStatusCmdExe {

    private final InstPoolMapper instPoolMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行启用/停用操作
     *
     * @param cmd 状态命令
     */
    public void execute(InstPoolStatusCmd cmd) {
        // 校验存在性
        InstPoolDO poolDO = instPoolMapper.selectById(cmd.getId());
        if (poolDO == null) {
            throw new RuntimeException("机构池不存在");
        }

        // 停用时校验池内无机构（可选：或仅警告不阻止）
        if (cmd.getStatus() != null && cmd.getStatus() == 0) {
            LambdaQueryWrapper<InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InstPoolInstitutionDO::getPoolId, cmd.getId());
            Long institutionCount = instPoolInstitutionMapper.selectCount(wrapper);
            if (institutionCount != null && institutionCount > 0) {
                throw new RuntimeException("该机构池下存在关联机构，无法停用");
            }
        }

        // 更新状态
        poolDO.setStatus(cmd.getStatus());
        instPoolMapper.updateById(poolDO);
    }
}
