package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.MenuEntity;
import java.util.List;

public interface MenuGateway {
    List<MenuEntity> listAll();
    void save(MenuEntity menu);
    MenuEntity getById(Long id);
    void deleteById(Long id);
}
