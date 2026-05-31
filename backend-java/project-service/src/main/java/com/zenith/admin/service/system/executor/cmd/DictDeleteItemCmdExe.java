package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictDeleteItemCmdExe {

    private final DictItemMapper dictItemMapper;

    public void execute(Long id) {
        dictItemMapper.deleteById(id);
    }
}
