package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FunctionDeleteCmdExe {

    private final FunctionMapper functionMapper;

    public void execute(Long id) {
        functionMapper.deleteById(id);
    }
}
