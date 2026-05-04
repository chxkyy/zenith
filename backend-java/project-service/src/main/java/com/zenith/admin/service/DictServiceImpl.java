package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.DictService;
import com.zenith.admin.dto.data.DictAddCmd;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemAddCmd;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.dto.data.DictItemUpdateCmd;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.dto.data.DictUpdateCmd;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.mapper.DictItemMapper;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

    @Override
    public List<DictDTO> listAll() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        List<DictDTO> dtos = dictConvertor.toDTOList(dictDOS);
        return dtos;
    }

    @Override
    public List<DictDTO> listByType(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        List<DictDTO> dtos = dictConvertor.toDTOList(dictDOS);
        return dtos;
    }

    @Override
    public void update(DictUpdateCmd cmd, Long currentUserId) {
        DictDO dictDO = new DictDO();
        dictDO.setId(cmd.getId());
        dictDO.setName(cmd.getName());
        dictDO.setType(cmd.getType());
        dictMapper.updateById(dictDO);
    }

    @Override
    public DictDTO getById(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        return dictConvertor.toDTO(dictDO);
    }

    @Override
    public PageInfo<DictDTO> page(DictPageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictDO::getName, query.getKeyword()).or().like(DictDO::getType, query.getKeyword());
            });
        }
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<DictDO> pageInfo = new com.github.pagehelper.PageInfo<>(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(pageInfo.getList());

        PageInfo<DictDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void delete(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        if (dictDO != null) {
            LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DictItemDO::getType, dictDO.getType());
            Long count = dictItemMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new com.alibaba.cola.exception.BizException("DICT_HAS_ITEMS", "该字典类型下存在字典项，请先删除所有字典项后再删除类型");
            }
        }
        dictMapper.deleteById(id);
    }

    @Override
    public void save(DictAddCmd cmd, Long currentUserId) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, cmd.getType());

        Long count = dictMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new com.alibaba.cola.exception.BizException("DICT_TYPE_EXIST", "字典类型编码已存在");
        }
        
        DictDO dictDO = new DictDO();
        dictDO.setName(cmd.getName());
        dictDO.setType(cmd.getType());
        dictMapper.insert(dictDO);
    }

    @Override
    public List<DictItemDTO> listItemsByType(String type) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type).orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(dictItemDOS);
        return dtos;
    }

    @Override
    public PageInfo<DictItemDTO> pageItems(DictItemPageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, query.getType());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictItemDO::getLabel, query.getKeyword()).or().like(DictItemDO::getDictValue, query.getKeyword());
            });
        }
        queryWrapper.orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<DictItemDO> pageInfo = new com.github.pagehelper.PageInfo<>(dictItemDOS);
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(pageInfo.getList());

        PageInfo<DictItemDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public void saveItem(DictItemAddCmd cmd, Long currentUserId) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, cmd.getType());
        queryWrapper.eq(DictItemDO::getDictValue, cmd.getDictValue());

        Long count = dictItemMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new com.alibaba.cola.exception.BizException("DICT_ITEM_VALUE_EXIST", "同类型下字典项值已存在");
        }
        
        DictItemDO dictItemDO = new DictItemDO();
        dictItemDO.setType(cmd.getType());
        dictItemDO.setLabel(cmd.getLabel());
        dictItemDO.setDictValue(cmd.getDictValue());
        dictItemDO.setSort(cmd.getSort());
        dictItemMapper.insert(dictItemDO);
    }

    @Override
    public void updateItem(DictItemUpdateCmd cmd, Long currentUserId) {
        DictItemDO dictItemDO = new DictItemDO();
        dictItemDO.setId(cmd.getId());
        dictItemDO.setType(cmd.getType());
        dictItemDO.setLabel(cmd.getLabel());
        dictItemDO.setDictValue(cmd.getDictValue());
        dictItemDO.setSort(cmd.getSort());
        dictItemMapper.updateById(dictItemDO);
    }

    @Override
    public void deleteItem(Long id) {
        dictItemMapper.deleteById(id);
    }

    @Override
    public DictItemDTO getItemById(Long id) {
        DictItemDO dictItemDO = dictItemMapper.selectById(id);
        return dictConvertor.toItemDTO(dictItemDO);
    }
}
