package com.zenith.admin.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.FunctionService;
import com.zenith.admin.dto.data.FunctionAddCmd;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.data.FunctionPageQuery;
import com.zenith.admin.dto.data.FunctionUpdateCmd;
import com.zenith.admin.service.system.executor.converter.FunctionConvertor;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {

    private final FunctionMapper functionMapper;
    private final FunctionConvertor functionConvertor;

    @Override
    public List<FunctionDTO> listByMenuId(Long menuId) {
        LambdaQueryWrapper<FunctionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FunctionDO::getMenuId, menuId);
        queryWrapper.orderByAsc(FunctionDO::getSort, FunctionDO::getId);
        List<FunctionDO> functionDOS = functionMapper.selectList(queryWrapper);
        List<FunctionDTO> dtos = functionConvertor.toDTOList(functionDOS);
        return dtos;
    }

    @Override
    public List<FunctionDTO> listAll() {
        LambdaQueryWrapper<FunctionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(FunctionDO::getSort, FunctionDO::getId);
        List<FunctionDO> functionDOS = functionMapper.selectList(queryWrapper);
        return functionConvertor.toDTOList(functionDOS);
    }

    @Override
    public PageInfo<FunctionDTO> page(FunctionPageQuery query) {
        LambdaQueryWrapper<FunctionDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(FunctionDO::getName, query.getKeyword());
        }

        if (query.getType() != null && !query.getType().isEmpty()) {
            queryWrapper.eq(FunctionDO::getType, query.getType());
        }

        if (query.getMenuId() != null) {
            queryWrapper.eq(FunctionDO::getMenuId, query.getMenuId());
        }

        queryWrapper.orderByAsc(FunctionDO::getSort);

        PageInfo<FunctionDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> functionMapper.selectList(queryWrapper));
        List<FunctionDTO> dtos = functionConvertor.toDTOList(pageInfo.getList());

        PageInfo<FunctionDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void save(FunctionAddCmd cmd, Long currentUserId) {
        FunctionDO functionDO = new FunctionDO();
        functionDO.setName(cmd.getName());
        functionDO.setType(cmd.getType());
        functionDO.setMenuId(cmd.getMenuId());
        functionDO.setPermission(cmd.getPermission());
        functionDO.setSort(cmd.getSort());
        functionDO.setStatus(cmd.getStatus());
        functionMapper.insert(functionDO);
    }

    @Override
    public void update(FunctionUpdateCmd cmd, Long currentUserId) {
        FunctionDO functionDO = new FunctionDO();
        functionDO.setId(cmd.getId());
        functionDO.setName(cmd.getName());
        functionDO.setType(cmd.getType());
        functionDO.setMenuId(cmd.getMenuId());
        functionDO.setPermission(cmd.getPermission());
        functionDO.setSort(cmd.getSort());
        functionDO.setStatus(cmd.getStatus());
        functionMapper.updateById(functionDO);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        functionMapper.deleteById(id);
    }

    @Override
    public FunctionDTO getById(Long id) {
        FunctionDO functionDO = functionMapper.selectById(id);
        return functionConvertor.toDTO(functionDO);
    }
}
