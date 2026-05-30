package com.zenith.admin.service;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.dto.data.MenuAddCmd;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuToggleStatusCmd;
import com.zenith.admin.dto.data.MenuUpdateCmd;
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
    public List<MenuDTO> listAll() {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(MenuDO::getSort);
        List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
        List<MenuDTO> dtos = menuConvertor.toDTOList(menuDOS);
        return dtos;
    }

    @Override
    public PageInfo<MenuDTO> page(MenuPageQuery query) {
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

        PageInfo<MenuDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> menuMapper.selectList(queryWrapper));
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
    public void save(MenuAddCmd cmd, Long currentUserId) {
        MenuDO menuDO = new MenuDO();
        menuDO.setName(cmd.getName());
        menuDO.setPath(cmd.getPath());
        menuDO.setComponent(cmd.getComponent());
        menuDO.setType(cmd.getType());
        menuDO.setParentId(cmd.getParentId());
        menuDO.setSort(cmd.getSort());
        menuDO.setIcon(cmd.getIcon());
        menuDO.setStatus(cmd.getStatus());
        menuDO.setPermission(cmd.getPermission());
        menuMapper.insert(menuDO);
    }

    @Override
    public void update(MenuUpdateCmd cmd, Long currentUserId) {
        MenuDO menuDO = new MenuDO();
        menuDO.setId(cmd.getId());
        menuDO.setName(cmd.getName());
        menuDO.setPath(cmd.getPath());
        menuDO.setComponent(cmd.getComponent());
        menuDO.setType(cmd.getType());
        menuDO.setParentId(cmd.getParentId());
        menuDO.setSort(cmd.getSort());
        menuDO.setIcon(cmd.getIcon());
        menuDO.setStatus(cmd.getStatus());
        menuDO.setPermission(cmd.getPermission());
        menuMapper.updateById(menuDO);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        menuMapper.deleteById(id);
    }

    @Override
    public MenuDTO getById(Long id) {
        MenuDO menuDO = menuMapper.selectById(id);
        return menuConvertor.toDTO(menuDO);
    }

    @Override
    public void updateParent(MenuUpdateParentCmd cmd, Long currentUserId) {
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

            int targetDepth = getDepth(newParentId);
            int maxChildDepth = getMaxChildDepth(id);
            if (targetDepth + 1 + maxChildDepth > 3) {
                throw new BizException("MAX_DEPTH_EXCEEDED", 
                    String.format("移动后菜单层级将超过3层（目标层级：%d，子树最大深度：%d），无法完成此操作", 
                        targetDepth, maxChildDepth));
            }
        }

        currentMenu.setParentId(newParentId);
        menuMapper.updateById(currentMenu);

        reorderSiblings(newParentId, currentUserId);
    }

    @Override
    public void reorder(MenuReorderCmd cmd, Long currentUserId) {
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
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }

    @Override
    public void toggleStatus(MenuToggleStatusCmd cmd, Long currentUserId) {
        Long id = cmd.getId();

        MenuDO menuDO = menuMapper.selectById(id);
        if (menuDO == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        Integer currentStatus = menuDO.getStatus();
        Integer newStatus = (currentStatus != null && currentStatus == 1) ? 0 : 1;

        menuDO.setStatus(newStatus);
        menuMapper.updateById(menuDO);
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

    private int getDepth(Long menuId) {
        int depth = 1;
        Long currentId = menuId;
        while (currentId != null) {
            MenuDO menu = menuMapper.selectById(currentId);
            if (menu == null || menu.getParentId() == null || menu.getParentId() == 0L) {
                break;
            }
            currentId = menu.getParentId();
            depth++;
        }
        return depth;
    }

    private int getMaxChildDepth(Long menuId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, menuId);
        List<MenuDO> children = menuMapper.selectList(queryWrapper);

        if (children.isEmpty()) {
            return 0;
        }

        int maxChildDepth = 0;
        for (MenuDO child : children) {
            int childDepth = 1 + getMaxChildDepth(child.getId());
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }
        return maxChildDepth;
    }

    private void reorderSiblings(Long parentId, Long currentUserId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            queryWrapper.eq(MenuDO::getParentId, parentId);
        } else {
            queryWrapper.isNull(MenuDO::getParentId);
        }
        queryWrapper.orderByAsc(MenuDO::getSort).orderByAsc(MenuDO::getId);
        List<MenuDO> siblings = menuMapper.selectList(queryWrapper);

        int sortValue = SORT_STEP;
        for (MenuDO menu : siblings) {
            menu.setSort(sortValue);
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }

    private static final int SORT_STEP = 10;
}
