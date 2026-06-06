package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.service.system.executor.converter.MenuConvertor;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuListAllQryExe {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    public List<MenuDTO> execute() {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(MenuDO::getSort);
        List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
        return menuConvertor.toDTOList(menuDOS);
    }
}
