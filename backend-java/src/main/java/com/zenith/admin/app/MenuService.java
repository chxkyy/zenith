package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.dto.MenuDTO;
import com.zenith.admin.dto.MenuPageQuery;
import com.zenith.admin.infrastructure.convertor.MenuConvertor;
import com.zenith.admin.infrastructure.dataobject.MenuDO;
import com.zenith.admin.infrastructure.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    public MultiResponse<MenuDTO> listAll() {
        List<MenuDO> menuDOS = menuMapper.selectList(null);
        List<MenuEntity> entities = menuConvertor.toEntityList(menuDOS);
        List<MenuDTO> dtos = menuConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public PageInfo<MenuDTO> page(MenuPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(MenuDO::getName, query.getKeyword())
                       .or().like(MenuDO::getPath, query.getKeyword());
            });
        }

        if (query.getType() != null && !query.getType().isEmpty()) {
            queryWrapper.eq(MenuDO::getType, query.getType());
        }

        if (query.getParentId() != null) {
            queryWrapper.eq(MenuDO::getParentId, query.getParentId());
        }

        queryWrapper.orderByAsc(MenuDO::getSort);
        List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
        PageInfo<MenuDO> pageInfo = new PageInfo<>(menuDOS);
        List<MenuEntity> entities = menuConvertor.toEntityList(pageInfo.getList());
        List<MenuDTO> dtos = menuConvertor.toDTOList(entities);

        PageInfo<MenuDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
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
