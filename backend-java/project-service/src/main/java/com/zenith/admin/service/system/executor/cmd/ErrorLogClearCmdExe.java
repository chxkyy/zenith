package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.mapper.ErrorLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ErrorLogClearCmdExe {

    private final ErrorLogMapper errorLogMapper;

    public void execute(int months) {
        LambdaQueryWrapper<ErrorLogDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(ErrorLogDO::getCreatedTime, LocalDateTime.now().minusMonths(months));
        errorLogMapper.delete(queryWrapper);
    }
}
