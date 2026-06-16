package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dataobject.InstInstitutionDO;
import com.zenith.admin.dataobject.InstPoolInstitutionDO;
import com.zenith.admin.dto.inst.cmd.InstPoolAddInstitutionCmd;
import com.zenith.admin.mapper.InstPoolInstitutionMapper;
import com.zenith.admin.mapper.InstPoolMapper;
import com.zenith.admin.mapper.InstInstitutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 机构入池执行器
 */
@Component
@RequiredArgsConstructor
public class InstAddToPoolCmdExe {

    private final InstPoolMapper instPoolMapper;
    private final InstInstitutionMapper instInstitutionMapper;
    private final InstPoolInstitutionMapper instPoolInstitutionMapper;

    /**
     * 执行入池操作
     *
     * @param cmd 入池命令
     */
    public void execute(InstPoolAddInstitutionCmd cmd) {
        // 校验池是否存在
        InstPoolDO poolDO = instPoolMapper.selectById(cmd.getPoolId());
        if (poolDO == null) {
            throw new RuntimeException("机构池不存在");
        }

        // 校验机构是否存在
        InstInstitutionDO institutionDO = instInstitutionMapper.selectById(cmd.getInstitutionId());
        if (institutionDO == null) {
            throw new RuntimeException("机构不存在");
        }

        // 校验尚未在池中（避免重复入池）
        LambdaQueryWrapper<InstPoolInstitutionDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolInstitutionDO::getPoolId, cmd.getPoolId())
               .eq(InstPoolInstitutionDO::getInstitutionId, cmd.getInstitutionId());
        Long count = instPoolInstitutionMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("该机构已在此机构池中");
        }

        // 插入关联记录
        InstPoolInstitutionDO relationDO = new InstPoolInstitutionDO();
        relationDO.setPoolId(cmd.getPoolId());
        relationDO.setInstitutionId(cmd.getInstitutionId());
        relationDO.setRemark(cmd.getRemark());
        relationDO.setAddedTime(LocalDateTime.now());

        instPoolInstitutionMapper.insert(relationDO);
    }
}
