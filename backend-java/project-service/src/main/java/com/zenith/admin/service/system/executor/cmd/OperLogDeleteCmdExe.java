package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.OperLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperLogDeleteCmdExe {

    private final OperLogMapper operLogMapper;

    public void execute(Long id) {
        operLogMapper.deleteById(id);
    }
}
