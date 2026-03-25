package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.gateway.MenuGateway;
import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.dto.MenuDTO;
import com.zenith.admin.infrastructure.convertor.MenuConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuGateway menuGateway;

    @Autowired
    private MenuConvertor menuConvertor;

    public MultiResponse<MenuDTO> listAll() {
        List<MenuEntity> entities = menuGateway.listAll();
        List<MenuDTO> dtos = menuConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(MenuDTO menuDTO) {
        MenuEntity entity = menuConvertor.toEntity(menuDTO);
        menuGateway.save(entity);
    }

    public void update(MenuDTO menuDTO) {
        MenuEntity entity = menuConvertor.toEntity(menuDTO);
        menuGateway.save(entity);
    }

    public void delete(Long id) {
        menuGateway.deleteById(id);
    }

    public MenuDTO getById(Long id) {
        MenuEntity entity = menuGateway.getById(id);
        return menuConvertor.toDTO(entity);
    }
}
