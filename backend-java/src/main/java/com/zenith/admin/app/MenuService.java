package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.dto.MenuDTO;
import com.zenith.admin.infrastructure.convertor.MenuConvertor;
import com.zenith.admin.infrastructure.dataobject.MenuDO;
import com.zenith.admin.infrastructure.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuConvertor menuConvertor;

    public MultiResponse<MenuDTO> listAll() {
        List<MenuDO> menuDOS = menuMapper.selectList(null);
        List<MenuEntity> entities = menuConvertor.toEntityList(menuDOS);
        List<MenuDTO> dtos = menuConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(MenuDTO menuDTO) {
        MenuEntity entity = menuConvertor.toEntity(menuDTO);
        MenuDO menuDO = menuConvertor.toDataObject(entity);
        if (menuDO.getId() == null) {
            menuMapper.insert(menuDO);
        } else {
            menuMapper.updateById(menuDO);
        }
    }

    public void update(MenuDTO menuDTO) {
        MenuEntity entity = menuConvertor.toEntity(menuDTO);
        MenuDO menuDO = menuConvertor.toDataObject(entity);
        menuMapper.updateById(menuDO);
    }

    public void delete(Long id) {
        menuMapper.deleteById(id);
    }

    public MenuDTO getById(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        MenuEntity entity = menuConvertor.toEntity(menuDO);
        return menuConvertor.toDTO(entity);
    }
}
