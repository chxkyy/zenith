package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dto.inst.cmd.InstPoolCreateCmd;
import com.zenith.admin.mapper.InstPoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 创建机构池执行器
 */
@Component
@RequiredArgsConstructor
public class InstPoolCreateCmdExe {

    private final InstPoolMapper instPoolMapper;

    /**
     * 执行创建机构池
     *
     * @param cmd 创建命令
     * @return 池ID
     */
    public Long execute(InstPoolCreateCmd cmd) {
        // 校验名称不重复
        LambdaQueryWrapper<InstPoolDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolDO::getName, cmd.getName());
        Long count = instPoolMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("机构池名称已存在");
        }

        // 构建DO并插入
        InstPoolDO poolDO = new InstPoolDO();
        poolDO.setName(cmd.getName());
        poolDO.setPoolType(cmd.getPoolType());
        poolDO.setDescription(cmd.getDescription());
        poolDO.setStatus(1); // 默认启用
        poolDO.setCreateUserId(UserContext.getUserId());

        instPoolMapper.insert(poolDO);
        return poolDO.getId();
    }
}
