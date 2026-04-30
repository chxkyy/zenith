package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuUpdateParentCmd;
import com.zenith.admin.dto.data.MenuReorderCmd;
import com.zenith.admin.MenuConvertor;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    @Override
    public MultiResponse<MenuDTO> listAll() {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(MenuDO::getSort);
        List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
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

    @Override
    public void updateParent(MenuUpdateParentCmd cmd) {
        Long id = cmd.getId();
        Long newParentId = cmd.getNewParentId();

        MenuDO currentMenu = menuMapper.selectById(id);
        if (currentMenu == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        if (newParentId != null) {
            MenuDO parentMenu = menuMapper.selectById(newParentId);
            if (parentMenu == null) {
                throw new BizException("PARENT_MENU_NOT_EXIST", "目标父菜单不存在");
            }
            if (isDescendant(id, newParentId)) {
                throw new BizException("MENU_CYCLE_REFERENCE", "不能将菜单移动到自己的子菜单下");
            }
        }

        currentMenu.setParentId(newParentId);
        currentMenu.setUpdateTime(LocalDateTime.now());
        currentMenu.setUpdateUserId(1L);
        menuMapper.updateById(currentMenu);

        reorderSiblings(newParentId);
    }

    @Override
    public void reorder(MenuReorderCmd cmd) {
        Long id = cmd.getId();
        Integer targetIndex = cmd.getTargetIndex();

        MenuDO currentMenu = menuMapper.selectById(id);
        if (currentMenu == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        Long parentId = currentMenu.getParentId();

        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, parentId);
        queryWrapper.orderByAsc(MenuDO::getSort).orderByAsc(MenuDO::getId);
        List<MenuDO> siblings = menuMapper.selectList(queryWrapper);

        MenuDO movedMenu = null;
        for (MenuDO m : siblings) {
            if (m.getId().equals(id)) {
                movedMenu = m;
                break;
            }
        }

        if (movedMenu != null) {
            siblings.remove(movedMenu);
        } else {
            return;
        }

        int safeIndex = Math.max(0, Math.min(targetIndex, siblings.size()));
        siblings.add(safeIndex, movedMenu);

        int sortValue = SORT_STEP;
        LocalDateTime now = LocalDateTime.now();
        for (MenuDO menu : siblings) {
            menu.setSort(sortValue);
            menu.setUpdateUserId(1L);
            menu.setUpdateTime(now);
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }

    private boolean isDescendant(Long ancestorId, Long descendantId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, descendantId);
        List<MenuDO> children = menuMapper.selectList(queryWrapper);

        for (MenuDO child : children) {
            if (child.getId().equals(ancestorId)) {
                return true;
            }
            if (isDescendant(ancestorId, child.getId())) {
                return true;
            }
        }
        return false;
    }

    private void reorderSiblings(Long parentId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            queryWrapper.eq(MenuDO::getParentId, parentId);
        } else {
            queryWrapper.isNull(MenuDO::getParentId);
        }
        queryWrapper.orderByAsc(MenuDO::getSort).orderByAsc(MenuDO::getId);
        List<MenuDO> siblings = menuMapper.selectList(queryWrapper);

        int sortValue = SORT_STEP;
        LocalDateTime now = LocalDateTime.now();
        for (MenuDO menu : siblings) {
            menu.setSort(sortValue);
            menu.setUpdateUserId(1L);
            menu.setUpdateTime(now);
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }

    private static final int SORT_STEP = 10;
}
