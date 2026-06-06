package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuDeleteCmdExe {

    private final MenuMapper menuMapper;

    public void execute(Long id) {
        menuMapper.deleteById(id);
    }
}
