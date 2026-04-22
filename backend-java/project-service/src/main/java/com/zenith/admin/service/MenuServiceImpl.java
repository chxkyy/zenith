package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.MenuConvertor;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    @Override
    public MultiResponse<MenuDTO> listAll() {
        List<MenuDO> menuDOS = menuMapper.selectList(null);
        List<MenuDTO> dtos = menuConvertor.toDTOList(menuDOS);
        return MultiResponse.of(dtos);
    }

    @Override
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
        List<MenuDTO> dtos = menuConvertor.toDTOList(pageInfo.getList());

        PageInfo<MenuDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void save(MenuDTO menuDTO) {
        MenuDO menuDO = menuConvertor.toDataObject(menuDTO);
        Long currentUserId = 1L;
        if (menuDO.getId() == null) {
            menuDO.setCreateUserId(currentUserId);
            menuDO.setCreatedTime(java.time.LocalDateTime.now());
            menuMapper.insert(menuDO);
        } else {
            menuDO.setUpdateUserId(currentUserId);
            menuDO.setUpdateTime(java.time.LocalDateTime.now());
            menuMapper.updateById(menuDO);
        }
    }

    @Override
    public void update(MenuDTO menuDTO) {
        MenuDO menuDO = menuConvertor.toDataObject(menuDTO);
        Long currentUserId = 1L;
        menuDO.setUpdateUserId(currentUserId);
        menuDO.setUpdateTime(java.time.LocalDateTime.now());
        menuMapper.updateById(menuDO);
    }

    @Override
    public void delete(Long id) {
        menuMapper.deleteById(id);
    }

    @Override
    public MenuDTO getById(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        return menuConvertor.toDTO(menuDO);
    }
}
