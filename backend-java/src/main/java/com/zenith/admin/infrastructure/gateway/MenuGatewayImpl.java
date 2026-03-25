package com.zenith.admin.infrastructure.gateway;

import com.zenith.admin.domain.gateway.MenuGateway;
import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.infrastructure.convertor.MenuConvertor;
import com.zenith.admin.infrastructure.dataobject.MenuDO;
import com.zenith.admin.infrastructure.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuGatewayImpl implements MenuGateway {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuConvertor menuConvertor;

    @Override
    public List<MenuEntity> listAll() {
        List<MenuDO> menuDOS = menuMapper.selectList(null);
        return menuConvertor.toEntityList(menuDOS);
    }

    @Override
    public void save(MenuEntity menu) {
        MenuDO menuDO = menuConvertor.toDataObject(menu);
        if (menuDO.getId() == null) {
            menuMapper.insert(menuDO);
        } else {
            menuMapper.updateById(menuDO);
        }
    }

    @Override
    public MenuEntity getById(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        return menuConvertor.toEntity(menuDO);
    }

    @Override
    public void deleteById(Long id) {
        menuMapper.deleteById(id);
    }
}
