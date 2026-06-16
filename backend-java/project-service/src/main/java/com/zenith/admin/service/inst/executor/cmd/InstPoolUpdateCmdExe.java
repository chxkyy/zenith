package com.zenith.admin.service.inst.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dataobject.InstPoolDO;
import com.zenith.admin.dto.inst.cmd.InstPoolUpdateCmd;
import com.zenith.admin.mapper.InstPoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 编辑机构池执行器
 */
@Component
@RequiredArgsConstructor
public class InstPoolUpdateCmdExe {

    private final InstPoolMapper instPoolMapper;

    /**
     * 执行编辑机构池
     *
     * @param cmd 编辑命令
     */
    public void execute(InstPoolUpdateCmd cmd) {
        // 校验存在性
        InstPoolDO existingDO = instPoolMapper.selectById(cmd.getId());
        if (existingDO == null) {
            throw new RuntimeException("机构池不存在");
        }

        // 校验名称唯一性（排除自身）
        LambdaQueryWrapper<InstPoolDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InstPoolDO::getName, cmd.getName())
               .ne(InstPoolDO::getId, cmd.getId());
        Long count = instPoolMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("机构池名称已存在");
        }

        // 更新DO
        existingDO.setName(cmd.getName());
        existingDO.setPoolType(cmd.getPoolType());
        existingDO.setDescription(cmd.getDescription());
        existingDO.setUpdateUserId(UserContext.getUserId());

        instPoolMapper.updateById(existingDO);
    }
}
