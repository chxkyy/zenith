package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.service.system.executor.converter.MenuConvertor;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuGetByIdQryExe {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    public MenuDTO execute(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        return menuConvertor.toDTO(menuDO);
    }
}
