package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.ErrorLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorLogDeleteCmdExe {

    private final ErrorLogMapper errorLogMapper;

    public void execute(Long id) {
        errorLogMapper.deleteById(id);
    }
}
